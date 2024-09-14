package tw.school.rental_backend.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.school.rental_backend.model.chat.ChatMessage;
import tw.school.rental_backend.repository.mongo.ChatMessageRepository;

import org.springframework.data.domain.Pageable;
import tw.school.rental_backend.service.ChatService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void saveMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findChatMessages(String senderId, String recipientId, Pageable pageable) {
        return chatMessageRepository.findBySenderIdAndReceiverIdOrderByTimestampDesc(senderId, recipientId, pageable);
    }
}
