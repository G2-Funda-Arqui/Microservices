package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.publishers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.DoseAdministeredIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.DoseSkippedIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.MedicationRegisteredIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.StockLowIntegrationEvent;

import java.time.Instant;

@Component
public class MedicationIntegrationEventPublisher {
    private static final int VERSION = 1;

    private final RabbitTemplate rabbitTemplate;

    public MedicationIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishMedicationRegisteredFallback")
    public void publishMedicationRegistered(Medication medication) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.ROUTING_KEY_MEDICATION_REGISTERED,
                new MedicationRegisteredIntegrationEvent(
                        medication.getId(),
                        medication.getPatientId(),
                        medication.getName(),
                        Instant.now(),
                        VERSION));
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishDoseAdministeredFallback")
    public void publishDoseAdministered(Integer medicationId, Integer scheduleId, Long patientId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.ROUTING_KEY_DOSE_ADMINISTERED,
                new DoseAdministeredIntegrationEvent(medicationId, scheduleId, patientId, Instant.now(), VERSION));
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishDoseSkippedFallback")
    public void publishDoseSkipped(Integer medicationId, Integer scheduleId, Long patientId, String reason) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.ROUTING_KEY_DOSE_SKIPPED,
                new DoseSkippedIntegrationEvent(medicationId, scheduleId, patientId, reason, Instant.now(), VERSION));
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishStockLowFallback")
    public void publishStockLow(Medication medication) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.ROUTING_KEY_STOCK_LOW,
                new StockLowIntegrationEvent(
                        medication.getId(),
                        medication.getPatientId(),
                        medication.getName(),
                        medication.getStockQuantity(),
                        medication.getLowStockThreshold(),
                        Instant.now(),
                        VERSION));
    }

    private void publishMedicationRegisteredFallback(Medication medication, Throwable exception) {
        throw new IllegalStateException("RabbitMQ medication registered publishing failed", exception);
    }

    private void publishDoseAdministeredFallback(Integer medicationId, Integer scheduleId, Long patientId, Throwable exception) {
        throw new IllegalStateException("RabbitMQ dose administered publishing failed", exception);
    }

    private void publishDoseSkippedFallback(Integer medicationId, Integer scheduleId, Long patientId, String reason, Throwable exception) {
        throw new IllegalStateException("RabbitMQ dose skipped publishing failed", exception);
    }

    private void publishStockLowFallback(Medication medication, Throwable exception) {
        throw new IllegalStateException("RabbitMQ stock low publishing failed", exception);
    }
}
