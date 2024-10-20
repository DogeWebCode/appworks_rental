package tw.school.rental_backend.service;

import tw.school.rental_backend.model.chat.ChatMessage;

import java.util.List;
import java.util.Map;

public interface ChatService {

    void saveMessage(ChatMessage chatMessage);

    List<ChatMessage> findChatMessages(String senderId, String recipientId);

    List<String> findChatPartners(String currentUserId);

    void markMessagesAsRead(String currentUserId,String partnerId);

    Map<String, Integer> findUnreadMessageCount(String currentUserId);

    void startChat(String senderId, String receiverId);

}
