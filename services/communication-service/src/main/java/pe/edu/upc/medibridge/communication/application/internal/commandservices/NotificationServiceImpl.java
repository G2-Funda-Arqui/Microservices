package pe.edu.upc.medibridge.communication.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.communication.domain.model.documents.Notification;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationChannel;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationStatus;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationType;
import pe.edu.upc.medibridge.communication.domain.services.NotificationService;
import pe.edu.upc.medibridge.communication.infrastructure.persistence.mongodb.repositories.NotificationRepository;
import pe.edu.upc.medibridge.communication.infrastructure.websocket.RealtimeMessagingService;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final RealtimeMessagingService realtimeMessagingService;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            RealtimeMessagingService realtimeMessagingService) {
        this.notificationRepository = notificationRepository;
        this.realtimeMessagingService = realtimeMessagingService;
    }

    @Override
    public Notification createSystemNotification(
            Long recipientUserId,
            Long patientId,
            NotificationType type,
            String title,
            String message,
            String sourceEvent) {
        var notification = Notification.builder()
                .recipientUserId(recipientUserId)
                .patientId(patientId)
                .type(type)
                .channel(NotificationChannel.IN_APP)
                .status(NotificationStatus.UNREAD)
                .title(title)
                .message(message)
                .sourceEvent(sourceEvent)
                .createdAt(Instant.now())
                .build();
        var saved = notificationRepository.save(notification);
        realtimeMessagingService.sendNotification(saved);
        return saved;
    }

    @Override
    public List<Notification> createSystemNotifications(
            List<Long> recipientUserIds,
            Long patientId,
            NotificationType type,
            String title,
            String message,
            String sourceEvent) {
        return recipientUserIds.stream()
                .distinct()
                .map(recipientUserId -> createSystemNotification(recipientUserId, patientId, type, title, message, sourceEvent))
                .toList();
    }

    @Override
    public List<Notification> findByRecipientUserId(Long recipientUserId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId);
    }

    @Override
    public List<Notification> findUnreadByRecipientUserId(Long recipientUserId) {
        return notificationRepository.findByRecipientUserIdAndStatusOrderByCreatedAtDesc(recipientUserId, NotificationStatus.UNREAD);
    }

    @Override
    public Notification markAsRead(String notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(Instant.now());
        return notificationRepository.save(notification);
    }
}
