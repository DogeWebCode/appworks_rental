package tw.school.rental_backend.service;

import tw.school.rental_backend.model.chat.ChatMessage;

import java.util.List;

public interface ChatService {

    void saveMessage(ChatMessage chatMessage);

    List<ChatMessage> findChatMessages(String senderId, String recipientId);

    List<String> getChatPartners(String currentUserId);
}
