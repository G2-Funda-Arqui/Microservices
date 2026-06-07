package pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.events;

import java.time.Instant;

public record ClinicalAlertTriggeredIntegrationEvent(
        Long alertId,
        Long patientId,
        Long observationId,
        String severity,
        String message,
        Instant occurredAt,
        int version
) {
    public ClinicalAlertTriggeredIntegrationEvent(
            Long alertId,
            Long patientId,
            Long observationId,
            String severity,
            String message) {
        this(alertId, patientId, observationId, severity, message, Instant.now(), 1);
    }
}
