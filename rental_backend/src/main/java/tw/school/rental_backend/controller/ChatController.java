package tw.school.rental_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tw.school.rental_backend.model.chat.ChatMessage;
import tw.school.rental_backend.service.ChatService;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            chatMessage.setSenderId(principal.getName());
            chatService.saveMessage(chatMessage);
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId(),
                    "/queue/messages",
                    chatMessage
            );
        } else {
            // 處理 Principal 為 null 的情況
            System.out.println("Principal is null, cannot process message.");
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<?> getChatMessages(@RequestParam String receiverId, Pageable pageable, Authentication authentication) {
        String senderId = authentication.getName();
        List<ChatMessage> messages = chatService.findChatMessages(senderId, receiverId, pageable);
        return ResponseEntity.ok(messages);
    }
}
