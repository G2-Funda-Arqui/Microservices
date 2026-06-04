package pe.edu.upc.medibridge.profiles.infrastructure.messaging.events;

import java.time.Instant;

public record FamilyMemberAssignedToPatientIntegrationEvent(
        Long linkId,
        Long familyMemberProfileId,
        Long patientId,
        Instant occurredAt,
        int version
) {
    public FamilyMemberAssignedToPatientIntegrationEvent(Long linkId, Long familyMemberProfileId, Long patientId) {
        this(linkId, familyMemberProfileId, patientId, Instant.now(), 1);
    }
}