package pe.edu.upc.medibridge.profiles.infrastructure.messaging.publishers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.profiles.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.profiles.infrastructure.messaging.events.DoctorAssignedToPatientIntegrationEvent;
import pe.edu.upc.medibridge.profiles.infrastructure.messaging.events.FamilyMemberAssignedToPatientIntegrationEvent;
import pe.edu.upc.medibridge.profiles.infrastructure.messaging.events.PatientDeactivatedIntegrationEvent;
import pe.edu.upc.medibridge.profiles.infrastructure.messaging.events.PatientRegisteredIntegrationEvent;

@Component
public class ProfileIntegrationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ProfileIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishPatientRegisteredFallback")
    public void publishPatientRegistered(Long patientId, String fullName) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_PATIENT_REGISTERED,
                new PatientRegisteredIntegrationEvent(patientId, fullName));
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishPatientDeactivatedFallback")
    public void publishPatientDeactivated(Long patientId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_PATIENT_DEACTIVATED,
                new PatientDeactivatedIntegrationEvent(patientId));
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishDoctorAssignedToPatientFallback")
    public void publishDoctorAssignedToPatient(Long assignmentId, Long doctorProfileId, Long patientId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_DOCTOR_ASSIGNED_PATIENT,
                new DoctorAssignedToPatientIntegrationEvent(assignmentId, doctorProfileId, patientId));
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishFamilyMemberAssignedToPatientFallback")
    public void publishFamilyMemberAssignedToPatient(Long linkId, Long familyMemberProfileId, Long patientId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_FAMILY_ASSIGNED_PATIENT,
                new FamilyMemberAssignedToPatientIntegrationEvent(linkId, familyMemberProfileId, patientId));
    }

    private void publishPatientRegisteredFallback(Long patientId, String fullName, Throwable exception) {
        throw new IllegalStateException("RabbitMQ patient registered publishing failed", exception);
    }

    private void publishPatientDeactivatedFallback(Long patientId, Throwable exception) {
        throw new IllegalStateException("RabbitMQ patient deactivated publishing failed", exception);
    }

    private void publishDoctorAssignedToPatientFallback(Long assignmentId, Long doctorProfileId, Long patientId, Throwable exception) {
        throw new IllegalStateException("RabbitMQ doctor assignment publishing failed", exception);
    }

    private void publishFamilyMemberAssignedToPatientFallback(Long linkId, Long familyMemberProfileId, Long patientId, Throwable exception) {
        throw new IllegalStateException("RabbitMQ family assignment publishing failed", exception);
    }
}
