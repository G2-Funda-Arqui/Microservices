package pe.edu.upc.medibridge.iam.domain.model.events;

import java.time.Instant;

public record UserRegisteredEvent(Long userId, String username, Instant occurredAt, int version) {
    public UserRegisteredEvent(Long userId, String username) {
        this(userId, username, Instant.now(), 1);
    }
}
