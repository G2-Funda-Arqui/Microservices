package pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.ClinicalAlert;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.PatientHealthObservation;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.events.ClinicalAlertTriggeredIntegrationEvent;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.events.PatientHealthObservationRecordedIntegrationEvent;

@Component
public class HealthMonitoringIntegrationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public HealthMonitoringIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishObservationRecorded(PatientHealthObservation observation) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_OBSERVATION_RECORDED,
                new PatientHealthObservationRecordedIntegrationEvent(
                        observation.getId(),
                        observation.getPatientId(),
                        observation.getRecordedByDoctorProfileId(),
                        observation.getRecordedAt()));
    }

    public void publishClinicalAlertTriggered(ClinicalAlert alert) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_ALERT_CRITICAL_TRIGGERED,
                new ClinicalAlertTriggeredIntegrationEvent(
                        alert.getId(),
                        alert.getPatientId(),
                        alert.getObservationId(),
                        alert.getSeverity().name(),
                        alert.getMessage()));
    }
}
