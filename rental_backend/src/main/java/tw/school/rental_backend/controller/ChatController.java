package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.school.rental_backend.error.ErrorResponse;
import tw.school.rental_backend.model.chat.ChatMessage;
import tw.school.rental_backend.service.ChatService;

import java.security.Principal;
import java.util.List;

@Log4j2
@RequestMapping("/api/chat")
@RestController
public class ChatController {

    private final ChatService chatService;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/message")
    public void processMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            chatMessage.setSenderId(principal.getName());
            chatService.saveMessage(chatMessage);
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId(),
                    "/queue/message",
                    chatMessage
            );

            // 通知所有人更新聊天對象列表
            List<String> updatedChatPartners = chatService.getChatPartners(principal.getName());
            messagingTemplate.convertAndSend("/topic/chat/partners", updatedChatPartners);
        } else {
            log.warn("No principal found in header accessor.");
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<?> getChatMessages(@RequestParam String receiverId, Authentication authentication) {
        try {
            String senderId = authentication.getName();
            List<ChatMessage> messages = chatService.findChatMessages(senderId, receiverId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Failed to get chat messages.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("獲取歷史聊天記錄失敗"));
        }
    }


    @GetMapping("/partners")
    public ResponseEntity<?> getChatPartners(Authentication authentication) {
        try {
            // 從 Spring Security 中獲取當前使用者的 ID
            String currentUserId = authentication.getName();
            // 獲取過去有跟誰聊過的使用者列表
            List<String> chatPartners = chatService.getChatPartners(currentUserId);
            // 返回使用者列表
            return ResponseEntity.ok(chatPartners);
        } catch (Exception e) {
            log.error("Failed to get chat partners.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("獲取聊天列表失敗"));
        }
    }
}
