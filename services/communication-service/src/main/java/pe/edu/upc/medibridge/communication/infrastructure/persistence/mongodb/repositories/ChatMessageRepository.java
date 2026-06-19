package pe.edu.upc.medibridge.communication.infrastructure.persistence.mongodb.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.upc.medibridge.communication.domain.model.documents.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatIdOrderBySentAtAsc(String chatId);
}
