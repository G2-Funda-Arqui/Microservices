package pe.edu.upc.medibridge.communication.domain.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationChannel;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationStatus;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationType;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    @Indexed
    private Long recipientUserId;

    @Indexed
    private Long patientId;

    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String title;
    private String message;
    private String sourceEvent;
    private Instant createdAt;
    private Instant readAt;
}
