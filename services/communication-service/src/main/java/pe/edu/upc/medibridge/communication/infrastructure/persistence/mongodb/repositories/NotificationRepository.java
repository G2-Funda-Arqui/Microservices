package pe.edu.upc.medibridge.communication.infrastructure.persistence.mongodb.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.upc.medibridge.communication.domain.model.documents.Notification;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationStatus;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId);
    List<Notification> findByRecipientUserIdAndStatusOrderByCreatedAtDesc(Long recipientUserId, NotificationStatus status);
}
