package pe.edu.upc.medibridge.profiles.domain.model.events;

import java.time.Instant;

public record DoctorProfileCreatedEvent(Long doctorProfileId, Long userId, Instant occurredAt, int version) {
    public DoctorProfileCreatedEvent(Long doctorProfileId, Long userId) {
        this(doctorProfileId, userId, Instant.now(), 1);
    }
}
