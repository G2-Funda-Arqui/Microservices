package pe.edu.upc.medibridge.appointments.infrastructure.messaging.events;

import java.time.Instant;

public record FamilyMemberAssignedToPatientIntegrationEvent(
        Long linkId,
        Long familyMemberProfileId,
        Long patientId,
        Instant occurredAt,
        int version
) {
}
