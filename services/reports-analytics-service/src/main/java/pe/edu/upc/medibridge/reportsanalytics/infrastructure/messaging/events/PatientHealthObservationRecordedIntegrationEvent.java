package pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events;

import java.time.Instant;
import java.time.LocalDateTime;

public record PatientHealthObservationRecordedIntegrationEvent(Long observationId, Long patientId, Long recordedByDoctorProfileId, LocalDateTime recordedAt, Instant occurredAt, int version) {
}
