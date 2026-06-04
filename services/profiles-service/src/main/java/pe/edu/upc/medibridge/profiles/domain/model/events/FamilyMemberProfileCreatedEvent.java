package pe.edu.upc.medibridge.profiles.domain.model.events;

import java.time.Instant;

public record FamilyMemberProfileCreatedEvent(Long familyMemberProfileId, Long userId, Instant occurredAt, int version) {
    public FamilyMemberProfileCreatedEvent(Long familyMemberProfileId, Long userId) {
        this(familyMemberProfileId, userId, Instant.now(), 1);
    }
}
