package pe.edu.upc.medibridge.reportsanalytics.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events.PatientHealthObservationRecordedIntegrationEvent;

@Component
public class VitalSignRecordedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VitalSignRecordedEventHandler.class);

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_OBSERVATION_RECORDED)
    public void on(PatientHealthObservationRecordedIntegrationEvent event) {
        LOGGER.info("Health observation recorded event received by reports analytics: {}", event);
    }
}
