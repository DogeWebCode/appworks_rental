package tw.school.rental_backend.service.Impl;

import org.springframework.stereotype.Service;
import tw.school.rental_backend.message.RedisMessagePublisher;
import tw.school.rental_backend.model.chat.ChatMessage;
import tw.school.rental_backend.repository.mongo.chat.ChatMessageRepository;

import tw.school.rental_backend.service.ChatService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final RedisMessagePublisher redisMessagePublisher;

    public ChatServiceImpl(ChatMessageRepository chatMessageRepository, RedisMessagePublisher redisMessagePublisher) {
        this.chatMessageRepository = chatMessageRepository;
        this.redisMessagePublisher = redisMessagePublisher;
    }

    @Override
    public void saveMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());

        chatMessageRepository.save(chatMessage);
        redisMessagePublisher.publish(chatMessage);
    }

    @Override
    public List<ChatMessage> findChatMessages(String currentUserId, String partnerId) {
        List<ChatMessage> messages = chatMessageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                currentUserId, partnerId);

        // 過濾掉系統訊息
        return messages.stream()
                .filter(message -> !message.isSystemMessage()) // 只保留非系統訊息
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findChatPartners(String currentUserId) {
        List<ChatMessage> messages = chatMessageRepository.findChatPartners(currentUserId);

        // 使用 Set 去除重複的使用者
        Set<String> chatPartners = new HashSet<>();

        // 將每條訊息中的發送者和接收者都加入列表，但排除當前使用者自己
        for (ChatMessage message : messages) {
            if (!message.getSenderId().equals(currentUserId)) {
                chatPartners.add(message.getSenderId());
            }
            if (!message.getReceiverId().equals(currentUserId)) {
                chatPartners.add(message.getReceiverId());
            }
        }
        return new ArrayList<>(chatPartners);
    }

    @Override
    public void markMessagesAsRead(String currentUserId, String partnerId) {
        // 只查詢未讀訊息
        List<ChatMessage> chatMessages = chatMessageRepository.findUnreadMessagesBetweenUsers(currentUserId, partnerId);

        // 標記為已讀
        for (ChatMessage chatMessage : chatMessages) {
            chatMessage.setRead(true);
        }

        // 保存已讀訊息
        if (!chatMessages.isEmpty()) {
            chatMessageRepository.saveAll(chatMessages);
        }
    }

    @Override
    public Map<String, Integer> findUnreadMessageCount(String currentUserId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessagesByReceiverId(currentUserId);
        Map<String, Integer> unreadCounts = new HashMap<>();
        for (ChatMessage chatMessage : unreadMessages) {
            String senderId = chatMessage.getSenderId();
            unreadCounts.put(senderId, unreadCounts.getOrDefault(senderId, 0) + 1);
        }
        return unreadCounts;
    }

    @Override
    public void startChat(String senderId, String receiverId) {
        List<ChatMessage> existingMessages = chatMessageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(senderId, receiverId);
        if (existingMessages.isEmpty()) {
            ChatMessage systemMessage = new ChatMessage();
            systemMessage.setSenderId(senderId);
            systemMessage.setReceiverId(receiverId);
            systemMessage.setContent("");
            systemMessage.setSystemMessage(true);
            systemMessage.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(systemMessage);
        }
    }

}
