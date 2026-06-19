package pe.edu.upc.medibridge.communication.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationType;
import pe.edu.upc.medibridge.communication.domain.services.NotificationService;
import pe.edu.upc.medibridge.communication.infrastructure.acl.ProfilesServiceClient;
import pe.edu.upc.medibridge.communication.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.communication.infrastructure.messaging.events.CriticalAlertTriggeredIntegrationEvent;

@Component
public class ClinicalEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicalEventHandler.class);

    private final NotificationService notificationService;
    private final ProfilesServiceClient profilesServiceClient;

    public ClinicalEventHandler(
            NotificationService notificationService,
            ProfilesServiceClient profilesServiceClient) {
        this.notificationService = notificationService;
        this.profilesServiceClient = profilesServiceClient;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_ALERT_CRITICAL_TRIGGERED)
    public void handleCriticalAlert(CriticalAlertTriggeredIntegrationEvent event) {
        var recipientIds = profilesServiceClient.getCareTeamUserIds(event.patientId());
        notificationService.createSystemNotifications(
                recipientIds,
                event.patientId(),
                NotificationType.CRITICAL_ALERT,
                "Alerta clinica critica",
                event.message(),
                RabbitMQConfiguration.ROUTING_KEY_ALERT_CRITICAL_TRIGGERED);
        LOGGER.info("Critical alert notifications created for patientId={} recipients={}", event.patientId(), recipientIds);
    }
}
