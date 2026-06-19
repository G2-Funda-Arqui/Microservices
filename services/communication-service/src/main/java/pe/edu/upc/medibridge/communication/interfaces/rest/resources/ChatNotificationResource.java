package pe.edu.upc.medibridge.communication.interfaces.rest.resources;

import java.time.Instant;

public record ChatNotificationResource(
        String id,
        Long senderUserId,
        Long recipientUserId,
        String content,
        Instant sentAt) {
}
