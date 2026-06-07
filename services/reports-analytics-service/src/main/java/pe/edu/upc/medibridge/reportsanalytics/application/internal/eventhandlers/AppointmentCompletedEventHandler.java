package pe.edu.upc.medibridge.reportsanalytics.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events.AppointmentScheduledIntegrationEvent;

@Component
public class AppointmentCompletedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentCompletedEventHandler.class);

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_APPOINTMENT_SCHEDULED)
    public void on(AppointmentScheduledIntegrationEvent event) {
        LOGGER.info("Appointment scheduled event received by reports analytics: {}", event);
    }
}
