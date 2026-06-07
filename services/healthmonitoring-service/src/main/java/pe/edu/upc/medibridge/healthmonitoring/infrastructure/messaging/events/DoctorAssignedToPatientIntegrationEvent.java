package pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.events;

import java.time.Instant;

public record DoctorAssignedToPatientIntegrationEvent(
        Long assignmentId,
        Long doctorProfileId,
        Long patientId,
        Instant occurredAt,
        int version
) {
}
