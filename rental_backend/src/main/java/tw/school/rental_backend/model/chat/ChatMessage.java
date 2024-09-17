package tw.school.rental_backend.model.chat;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "message")
@Data
public class ChatMessage {

    @Id
    private String id;
    private String senderId;
    private String receiverId;
    private String message;
    private LocalDateTime timestamp;
}
