package pe.edu.upc.medibridge.communication.infrastructure.persistence.mongodb.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.upc.medibridge.communication.domain.model.documents.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderUserIdAndRecipientUserId(Long senderUserId, Long recipientUserId);
}
