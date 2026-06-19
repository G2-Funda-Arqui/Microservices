package pe.edu.upc.medibridge.communication.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.NotificationType;
import pe.edu.upc.medibridge.communication.domain.services.NotificationService;
import pe.edu.upc.medibridge.communication.infrastructure.acl.ProfilesServiceClient;
import pe.edu.upc.medibridge.communication.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.communication.infrastructure.messaging.events.DoseAdministeredIntegrationEvent;
import pe.edu.upc.medibridge.communication.infrastructure.messaging.events.DoseSkippedIntegrationEvent;
import pe.edu.upc.medibridge.communication.infrastructure.messaging.events.StockLowIntegrationEvent;

@Component
public class MedicationEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MedicationEventHandler.class);

    private final NotificationService notificationService;
    private final ProfilesServiceClient profilesServiceClient;

    public MedicationEventHandler(
            NotificationService notificationService,
            ProfilesServiceClient profilesServiceClient) {
        this.notificationService = notificationService;
        this.profilesServiceClient = profilesServiceClient;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_DOSE_ADMINISTERED)
    public void handleDoseAdministered(DoseAdministeredIntegrationEvent event) {
        var recipientIds = profilesServiceClient.getCareTeamUserIds(event.patientId());
        notificationService.createSystemNotifications(
                recipientIds,
                event.patientId(),
                NotificationType.DOSE_ADMINISTERED,
                "Dosis administrada",
                "Se registro una dosis administrada para el medicamento " + event.medicationId() + ".",
                RabbitMQConfiguration.ROUTING_KEY_DOSE_ADMINISTERED);
        LOGGER.info("Dose administered notifications created for patientId={} recipients={}", event.patientId(), recipientIds);
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_DOSE_SKIPPED)
    public void handleDoseSkipped(DoseSkippedIntegrationEvent event) {
        var recipientIds = profilesServiceClient.getCareTeamUserIds(event.patientId());
        notificationService.createSystemNotifications(
                recipientIds,
                event.patientId(),
                NotificationType.DOSE_SKIPPED,
                "Dosis omitida",
                "Se omitio una dosis del medicamento " + event.medicationId() + ". Motivo: " + event.reason(),
                RabbitMQConfiguration.ROUTING_KEY_DOSE_SKIPPED);
        LOGGER.info("Dose skipped notifications created for patientId={} recipients={}", event.patientId(), recipientIds);
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_STOCK_LOW)
    public void handleStockLow(StockLowIntegrationEvent event) {
        var recipientIds = profilesServiceClient.getCareTeamUserIds(event.patientId());
        notificationService.createSystemNotifications(
                recipientIds,
                event.patientId(),
                NotificationType.STOCK_LOW,
                "Stock bajo",
                "Stock bajo para " + event.medicationName() + ". Stock actual: " + event.currentStock() + ".",
                RabbitMQConfiguration.ROUTING_KEY_STOCK_LOW);
        LOGGER.info("Stock low notifications created for patientId={} recipients={}", event.patientId(), recipientIds);
    }
}
