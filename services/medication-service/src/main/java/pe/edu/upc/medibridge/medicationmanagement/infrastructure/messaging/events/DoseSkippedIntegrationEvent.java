package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record DoseSkippedIntegrationEvent(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        String reason,
        Instant occurredAt,
        int version) {
}
