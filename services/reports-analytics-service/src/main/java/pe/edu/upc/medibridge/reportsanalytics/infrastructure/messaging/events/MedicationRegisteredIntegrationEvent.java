package pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events;

import java.time.Instant;

public record MedicationRegisteredIntegrationEvent(Integer medicationId, Long patientId, String medicationName, Instant occurredAt, int version) {
}
