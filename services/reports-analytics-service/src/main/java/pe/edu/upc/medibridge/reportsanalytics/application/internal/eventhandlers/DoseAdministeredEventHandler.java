package pe.edu.upc.medibridge.reportsanalytics.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events.DoseAdministeredIntegrationEvent;

@Component
public class DoseAdministeredEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoseAdministeredEventHandler.class);

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_DOSE_ADMINISTERED)
    public void on(DoseAdministeredIntegrationEvent event) {
        LOGGER.info("Dose administered event received by reports analytics: {}", event);
    }
}
