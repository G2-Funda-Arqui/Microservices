package pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events;

import java.time.Instant;

public record DoseAdministeredIntegrationEvent(Integer medicationId, Integer scheduleId, Long patientId, Instant occurredAt, int version) {
}
