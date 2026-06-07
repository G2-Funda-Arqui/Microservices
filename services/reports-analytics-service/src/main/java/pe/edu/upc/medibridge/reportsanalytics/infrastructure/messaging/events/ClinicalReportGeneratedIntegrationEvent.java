package pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events;

import java.time.Instant;

public record ClinicalReportGeneratedIntegrationEvent(Integer reportId, Long patientId, Instant occurredAt, int version) {
    public ClinicalReportGeneratedIntegrationEvent(Integer reportId, Long patientId) {
        this(reportId, patientId, Instant.now(), 1);
    }
}
