package pe.edu.upc.medibridge.profiles.infrastructure.messaging.events;

import java.time.Instant;

public record DoctorAssignedToPatientIntegrationEvent(
        Long assignmentId,
        Long doctorProfileId,
        Long patientId,
        Instant occurredAt,
        int version
) {
    public DoctorAssignedToPatientIntegrationEvent(Long assignmentId, Long doctorProfileId, Long patientId) {
        this(assignmentId, doctorProfileId, patientId, Instant.now(), 1);
    }
}