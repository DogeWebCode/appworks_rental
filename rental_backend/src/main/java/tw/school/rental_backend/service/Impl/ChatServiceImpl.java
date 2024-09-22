package tw.school.rental_backend.service.Impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tw.school.rental_backend.model.chat.ChatMessage;
import tw.school.rental_backend.repository.mongo.chat.ChatMessageRepository;

import tw.school.rental_backend.service.ChatService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatServiceImpl(ChatMessageRepository chatMessageRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void saveMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);

        // 保存後獲取最新的聊天對象並推送給所有人
        List<String> updatedChatPartners = getChatPartners(chatMessage.getSenderId());
        messagingTemplate.convertAndSend("/topic/chat/partners", updatedChatPartners);
    }

    @Override
    public List<ChatMessage> findChatMessages(String currentUserId, String partnerId) {
        return chatMessageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                currentUserId, partnerId);
    }

    @Override
    public List<String> getChatPartners(String currentUserId) {
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

        return new ArrayList<>(chatPartners); // 返回不包含當前使用者的聊天對象
    }
}
