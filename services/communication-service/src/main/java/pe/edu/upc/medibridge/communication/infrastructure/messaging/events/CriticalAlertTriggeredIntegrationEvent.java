package pe.edu.upc.medibridge.communication.infrastructure.messaging.events;

import java.time.Instant;

public record CriticalAlertTriggeredIntegrationEvent(
        Long alertId,
        Long patientId,
        Long observationId,
        String severity,
        String message,
        Instant occurredAt,
        int version) {
}
