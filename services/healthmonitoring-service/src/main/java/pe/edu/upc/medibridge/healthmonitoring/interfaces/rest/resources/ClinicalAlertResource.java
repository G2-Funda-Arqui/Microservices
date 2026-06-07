package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources;

import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.AlertSeverity;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.AlertStatus;

import java.time.LocalDateTime;

public record ClinicalAlertResource(
        Long id,
        Long patientId,
        Long observationId,
        AlertSeverity severity,
        AlertStatus status,
        String message,
        LocalDateTime triggeredAt) {
}
