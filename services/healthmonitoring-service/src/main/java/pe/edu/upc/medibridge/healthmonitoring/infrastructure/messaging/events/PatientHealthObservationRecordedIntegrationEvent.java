package pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.events;

import java.time.Instant;
import java.time.LocalDateTime;

public record PatientHealthObservationRecordedIntegrationEvent(
        Long observationId,
        Long patientId,
        Long recordedByDoctorProfileId,
        LocalDateTime recordedAt,
        Instant occurredAt,
        int version
) {
    public PatientHealthObservationRecordedIntegrationEvent(
            Long observationId,
            Long patientId,
            Long recordedByDoctorProfileId,
            LocalDateTime recordedAt) {
        this(observationId, patientId, recordedByDoctorProfileId, recordedAt, Instant.now(), 1);
    }
}
