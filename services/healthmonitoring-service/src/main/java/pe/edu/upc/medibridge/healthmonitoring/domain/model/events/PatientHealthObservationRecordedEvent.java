package pe.edu.upc.medibridge.healthmonitoring.domain.model.events;

import java.time.Instant;
import java.time.LocalDateTime;

public record PatientHealthObservationRecordedEvent(
        Long observationId,
        Long patientId,
        Long recordedByDoctorProfileId,
        LocalDateTime recordedAt,
        Instant occurredAt,
        int version) {

    public PatientHealthObservationRecordedEvent(
            Long observationId,
            Long patientId,
            Long recordedByDoctorProfileId,
            LocalDateTime recordedAt) {
        this(observationId, patientId, recordedByDoctorProfileId, recordedAt, Instant.now(), 1);
    }
}
