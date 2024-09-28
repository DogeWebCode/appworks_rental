package tw.school.rental_backend.repository.mongo.chat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.chat.ChatMessage;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query("{ $or: [ { $and: [ { 'senderId': ?0 }, { 'receiverId': ?1 } ] }, { $and: [ { 'receiverId': ?0 }, { 'senderId': ?1 } ] } ] }")
    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
            String currentUserId, String partnerId);

    @Query("{ $or: [ { 'senderId': ?0 }, { 'receiverId': ?0 } ] }")
    List<ChatMessage> findChatPartners(String currentUserId);

    @Query("{'receiverId': ?0, 'isRead': false}")
    List<ChatMessage> findUnreadMessagesByReceiverId(String receiverId);

    @Query("{ $or: [ { $and: [ { 'senderId': ?0 }, { 'receiverId': ?1 }, { 'isRead': false } ] }, { $and: [ { 'receiverId': ?0 }, { 'senderId': ?1 }, { 'isRead': false } ] } ] }")
    List<ChatMessage> findUnreadMessagesBetweenUsers(String currentUserId, String partnerId);

}
