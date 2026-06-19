package pe.edu.upc.medibridge.communication.interfaces.rest.resources;

import pe.edu.upc.medibridge.communication.domain.model.documents.Notification;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationChannel;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationStatus;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationType;

import java.time.Instant;

public record NotificationResource(
        String id,
        Long recipientUserId,
        Long patientId,
        NotificationType type,
        NotificationChannel channel,
        NotificationStatus status,
        String title,
        String message,
        String sourceEvent,
        Instant createdAt,
        Instant readAt) {

    public static NotificationResource from(Notification notification) {
        return new NotificationResource(
                notification.getId(),
                notification.getRecipientUserId(),
                notification.getPatientId(),
                notification.getType(),
                notification.getChannel(),
                notification.getStatus(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getSourceEvent(),
                notification.getCreatedAt(),
                notification.getReadAt());
    }
}
