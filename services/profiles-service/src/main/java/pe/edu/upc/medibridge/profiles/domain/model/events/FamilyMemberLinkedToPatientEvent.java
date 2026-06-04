package pe.edu.upc.medibridge.profiles.domain.model.events;

import java.time.Instant;

public record FamilyMemberLinkedToPatientEvent(
        Long linkId,
        Long familyMemberProfileId,
        Long patientId,
        Instant occurredAt,
        int version) {

    public FamilyMemberLinkedToPatientEvent(Long linkId, Long familyMemberProfileId, Long patientId) {
        this(linkId, familyMemberProfileId, patientId, Instant.now(), 1);
    }
}
