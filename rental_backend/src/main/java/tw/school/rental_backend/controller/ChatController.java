package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.error.ErrorResponse;
import tw.school.rental_backend.model.chat.ChatMessage;
import tw.school.rental_backend.service.ChatService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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

            // 發送訊息給接收者
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId(),
                    "/queue/message",
                    chatMessage
            );

            // 更新發送者和接收者的聊天夥伴列表
            List<String> senderChatPartners = chatService.findChatPartners(principal.getName());
            messagingTemplate.convertAndSendToUser(
                    principal.getName(), // 發送者
                    "/queue/partners",
                    senderChatPartners
            );

            List<String> receiverChatPartners = chatService.findChatPartners(chatMessage.getReceiverId());
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId(), // 接收者
                    "/queue/partners",
                    receiverChatPartners
            );
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
            String currentUserId = authentication.getName();
            // 獲取過去有跟誰聊過的使用者列表
            List<String> chatPartners = chatService.findChatPartners(currentUserId);
            return ResponseEntity.ok(chatPartners);
        } catch (Exception e) {
            log.error("Failed to get chat partners.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("獲取聊天列表失敗"));
        }
    }


    @PostMapping("/messages/read")
    public ResponseEntity<?> markMessagesAsRead(@RequestBody Map<String, String> requestBody, Authentication authentication) {
        try {
            String partnerId = requestBody.get("partnerId");
            String currentUserId = authentication.getName();
            chatService.markMessagesAsRead(currentUserId, partnerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to mark messages as read.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("標記訊息為已讀失敗"));
        }
    }

    @GetMapping("/messages/unread/count")
    public ResponseEntity<?> getUnreadMessageCount(Authentication authentication) {
        try {
            String currentUserId = authentication.getName();
            Map<String,Integer> unreadMessageCount = chatService.findUnreadMessageCount(currentUserId);
            return ResponseEntity.ok(unreadMessageCount);
        } catch (Exception e) {
            log.error("Failed to get unread message count.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("獲取未讀消息數量失敗"));
        }
    }
}
