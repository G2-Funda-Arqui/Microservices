package pe.edu.upc.medibridge.reportsanalytics.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events.DoseSkippedIntegrationEvent;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events.MedicationRegisteredIntegrationEvent;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events.StockLowIntegrationEvent;

@Component
public class MedicationEventsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MedicationEventsHandler.class);

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_MEDICATION_REGISTERED)
    public void onMedicationRegistered(MedicationRegisteredIntegrationEvent event) {
        LOGGER.info("Medication registered event received by reports analytics: {}", event);
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_DOSE_SKIPPED)
    public void onDoseSkipped(DoseSkippedIntegrationEvent event) {
        LOGGER.info("Dose skipped event received by reports analytics: {}", event);
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_STOCK_LOW)
    public void onStockLow(StockLowIntegrationEvent event) {
        LOGGER.info("Stock low event received by reports analytics: {}", event);
    }
}
