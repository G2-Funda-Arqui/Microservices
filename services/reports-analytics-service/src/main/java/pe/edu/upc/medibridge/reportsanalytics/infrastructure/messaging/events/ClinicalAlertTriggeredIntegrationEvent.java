package pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events;

import java.time.Instant;

public record ClinicalAlertTriggeredIntegrationEvent(Long alertId, Long patientId, Long observationId, String severity, String message, Instant occurredAt, int version) {
}
