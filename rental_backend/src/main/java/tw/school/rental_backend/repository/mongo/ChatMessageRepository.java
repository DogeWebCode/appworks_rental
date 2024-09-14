package tw.school.rental_backend.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.chat.ChatMessage;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampDesc(String senderId, String receiverId, Pageable pageable);

}
