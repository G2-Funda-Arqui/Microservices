package pe.edu.upc.medibridge.communication.infrastructure.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.communication.domain.model.documents.ChatMessage;
import pe.edu.upc.medibridge.communication.domain.model.documents.Notification;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.ChatNotificationResource;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.NotificationResource;

@Service
public class RealtimeMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendChatMessage(ChatMessage message) {
        messagingTemplate.convertAndSend(
                "/topic/users/" + message.getRecipientUserId() + "/messages",
                new ChatNotificationResource(message.getId(), message.getSenderUserId(), message.getRecipientUserId(), message.getContent(), message.getSentAt()));
    }

    public void sendNotification(Notification notification) {
        messagingTemplate.convertAndSend(
                "/topic/users/" + notification.getRecipientUserId() + "/notifications",
                NotificationResource.from(notification));
    }

    public void broadcastCareTeamNotification(Long patientId, Notification notification) {
        messagingTemplate.convertAndSend(
                "/topic/patients/" + patientId + "/notifications",
                NotificationResource.from(notification));
    }
}
