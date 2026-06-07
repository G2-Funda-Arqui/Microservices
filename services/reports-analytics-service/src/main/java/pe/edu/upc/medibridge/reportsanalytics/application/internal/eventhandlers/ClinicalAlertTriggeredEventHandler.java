package pe.edu.upc.medibridge.reportsanalytics.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events.ClinicalAlertTriggeredIntegrationEvent;

@Component
public class ClinicalAlertTriggeredEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicalAlertTriggeredEventHandler.class);

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_ALERT_CRITICAL_TRIGGERED)
    public void on(ClinicalAlertTriggeredIntegrationEvent event) {
        LOGGER.info("Clinical alert event received by reports analytics: {}", event);
    }
}
