package tw.school.rental_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.integration.redis.inbound.RedisInboundChannelAdapter;
import org.springframework.integration.redis.outbound.RedisPublishingMessageHandler;
import org.springframework.lang.NonNull;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import tw.school.rental_backend.middleware.JwtHandshakeInterceptor;
import tw.school.rental_backend.middleware.JwtTokenProvider;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${cors.allow.origins}")
    private String allowedOrigins;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisConnectionFactory redisConnectionFactory;

    public WebSocketConfig(JwtTokenProvider jwtTokenProvider, RedisConnectionFactory redisConnectionFactory) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/topic")
                .setTaskScheduler(heartBeatScheduler())
                .setHeartbeatValue(new long[]{10000, 10000});

        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("HeartbeatScheduler-");
        scheduler.initialize();
        return scheduler;
    }


    @Bean
    public RedisMessageListenerContainer webSocketRedisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }

    @Bean
    public RedisInboundChannelAdapter redisInboundChannelAdapter(RedisConnectionFactory redisConnectionFactory) {
        RedisInboundChannelAdapter adapter = new RedisInboundChannelAdapter(redisConnectionFactory);
        adapter.setTopics("chat-messages"); // 設置 Redis 訂閱的頻道名稱或模式
        adapter.setOutputChannel(myClientOutboundChannel()); // 指定消息的輸出通道
        return adapter;
    }

    @Bean
    public MessageChannel myClientOutboundChannel() {
        return new ExecutorSubscribableChannel();
    }

    @Bean
    public MessageHandler redisOutboundChannelAdapter() {
        RedisPublishingMessageHandler handler = new RedisPublishingMessageHandler(redisConnectionFactory);
        handler.setTopic("chat-messages");
        return handler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenProvider))
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(@NonNull ServerHttpRequest request, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
                        return (Authentication) attributes.get("SPRING_SECURITY_CONTEXT");
                    }
                })
                .setAllowedOrigins(allowedOrigins);
    }
}


