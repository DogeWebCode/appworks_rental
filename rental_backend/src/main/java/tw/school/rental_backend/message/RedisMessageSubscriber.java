package tw.school.rental_backend.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tw.school.rental_backend.model.chat.ChatMessage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Long> processedMessages = new ConcurrentHashMap<>();

    public RedisMessageSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        log.info("RedisMessageSubscriber instantiated");

        // 創建 ObjectMapper 並註冊 JavaTimeModule
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Received message from Redis on thread {}: {}", Thread.currentThread().getName(), messageBody);

            ChatMessage chatMessage = objectMapper.readValue(messageBody, ChatMessage.class);

            // 檢查消息是否已經處理過（根據消息ID檢查）
            if (isDuplicateMessage(chatMessage.getId())) {
                log.warn("Duplicate message received, skipping: {}", chatMessage.getId());
                return;
            }

            // Send the message to the receiver via WebSocket
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId(),
                    "/queue/message",
                    chatMessage
            );
        } catch (Exception e) {
            log.error("Failed to process WebSocket message", e);
        }
    }

    private boolean isDuplicateMessage(String messageId) {
        // 檢查 ConcurrentHashMap 是否已有該消息ID
        // 如果已存在，表示重複；如果不存在，將其存入並設置超時（例如5秒後自動刪除）
        Long currentTime = System.currentTimeMillis();
        if (processedMessages.putIfAbsent(messageId, currentTime) != null) {
            return true;
        }
        // 設置一定時間後自動刪除該消息ID，防止內存泄漏
        processedMessages.keySet().removeIf(key -> currentTime - processedMessages.get(key) > TimeUnit.SECONDS.toMillis(5));
        return false;
    }
}
