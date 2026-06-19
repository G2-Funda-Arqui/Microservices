package pe.edu.upc.medibridge.communication.interfaces.rest.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.communication.domain.services.NotificationService;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.NotificationResource;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/recipients/{recipientUserId}")
    public ResponseEntity<List<NotificationResource>> findByRecipientUserId(@PathVariable Long recipientUserId) {
        var notifications = notificationService.findByRecipientUserId(recipientUserId)
                .stream()
                .map(NotificationResource::from)
                .toList();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/recipients/{recipientUserId}/unread")
    public ResponseEntity<List<NotificationResource>> findUnreadByRecipientUserId(@PathVariable Long recipientUserId) {
        var notifications = notificationService.findUnreadByRecipientUserId(recipientUserId)
                .stream()
                .map(NotificationResource::from)
                .toList();
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResource> markAsRead(@PathVariable String notificationId) {
        return ResponseEntity.ok(NotificationResource.from(notificationService.markAsRead(notificationId)));
    }
}
