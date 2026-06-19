package pe.edu.upc.medibridge.communication.domain.services;

import pe.edu.upc.medibridge.communication.domain.model.documents.Notification;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationType;

import java.util.List;

public interface NotificationService {
    Notification createSystemNotification(Long recipientUserId, Long patientId, NotificationType type, String title, String message, String sourceEvent);
    List<Notification> createSystemNotifications(List<Long> recipientUserIds, Long patientId, NotificationType type, String title, String message, String sourceEvent);
    List<Notification> findByRecipientUserId(Long recipientUserId);
    List<Notification> findUnreadByRecipientUserId(Long recipientUserId);
    Notification markAsRead(String notificationId);
}
