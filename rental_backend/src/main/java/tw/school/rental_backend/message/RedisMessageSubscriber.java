package tw.school.rental_backend.message;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.school.rental_backend.model.chat.ChatMessage;

@Component
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisMessageSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;

        // 創建 ObjectMapper 並註冊 JavaTimeModule
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // 註冊 Java 8 日期模組
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用日期時間戳
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getBody(), ChatMessage.class);

            // Send the message to the receiver via WebSocket
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId(),
                    "/queue/message",
                    chatMessage
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
