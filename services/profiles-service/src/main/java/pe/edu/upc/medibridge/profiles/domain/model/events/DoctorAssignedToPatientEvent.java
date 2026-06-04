package pe.edu.upc.medibridge.profiles.domain.model.events;

import java.time.Instant;

public record DoctorAssignedToPatientEvent(
        Long assignmentId,
        Long doctorProfileId,
        Long patientId,
        Instant occurredAt,
        int version) {

    public DoctorAssignedToPatientEvent(Long assignmentId, Long doctorProfileId, Long patientId) {
        this(assignmentId, doctorProfileId, patientId, Instant.now(), 1);
    }
}
