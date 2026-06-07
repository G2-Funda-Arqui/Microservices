package pe.edu.upc.medibridge.reportsanalytics.domain.model.events;

import java.time.Instant;

public record ClinicalReportGeneratedEvent(Integer reportId, Long patientId, Instant occurredAt, int version) {
    public ClinicalReportGeneratedEvent(Integer reportId, Long patientId) {
        this(reportId, patientId, Instant.now(), 1);
    }
}
