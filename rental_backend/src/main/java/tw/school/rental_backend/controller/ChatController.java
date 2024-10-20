package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.model.chat.ChatMessage;
import tw.school.rental_backend.service.ChatService;
import tw.school.rental_backend.util.UserUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final MessageHandler redisOutboundAdapter;
    private final UserUtil userUtil;

    public ChatController(ChatService chatService, MessageHandler redisOutboundAdapter, UserUtil userUtil) {
        this.chatService = chatService;
        this.redisOutboundAdapter = redisOutboundAdapter;
        this.userUtil = userUtil;
    }

    @MessageMapping("/message")
    public void processMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String senderId = Objects.requireNonNull(headerAccessor.getUser()).getName();
        chatMessage.setSenderId(senderId);

        // 保存訊息到 MongoDB
        chatService.saveMessage(chatMessage);

        // 發送訊息到 Redis
        Message<ChatMessage> message = MessageBuilder.withPayload(chatMessage).build();
        redisOutboundAdapter.handleMessage(message);
        log.info("Sending message to Redis: {}", chatMessage);
    }

    @GetMapping("/messages")
    public ResponseEntity<?> getChatMessages(@RequestParam String receiverId) {
        String senderId = userUtil.getCurrentUserName();
        List<ChatMessage> messages = chatService.findChatMessages(senderId, receiverId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/partners")
    public ResponseEntity<?> getChatPartners() {
        String currentUserId = userUtil.getCurrentUserName();
        List<String> chatPartners = chatService.findChatPartners(currentUserId);
        return ResponseEntity.ok(chatPartners);
    }

    @PostMapping("/startChat")
    public ResponseEntity<?> startChat(@RequestBody Map<String, String> requestBody) {
        String senderId = userUtil.getCurrentUserName();
        String receiverId = requestBody.get("receiverId");
        chatService.startChat(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/messages/read")
    public ResponseEntity<?> markMessagesAsRead(@RequestBody Map<String, String> requestBody) {
        String partnerId = requestBody.get("partnerId");
        String currentUserId = userUtil.getCurrentUserName();
        chatService.markMessagesAsRead(currentUserId, partnerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages/unread/count")
    public ResponseEntity<?> getUnreadMessageCount() {
        String currentUserId = userUtil.getCurrentUserName();
        Map<String, Integer> unreadMessageCount = chatService.findUnreadMessageCount(currentUserId);
        return ResponseEntity.ok(unreadMessageCount);
    }
}
