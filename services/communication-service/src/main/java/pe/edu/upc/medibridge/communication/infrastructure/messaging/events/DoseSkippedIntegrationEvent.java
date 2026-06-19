package pe.edu.upc.medibridge.communication.infrastructure.messaging.events;

import java.time.Instant;

public record DoseSkippedIntegrationEvent(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        String reason,
        Instant occurredAt,
        int version) {
}
