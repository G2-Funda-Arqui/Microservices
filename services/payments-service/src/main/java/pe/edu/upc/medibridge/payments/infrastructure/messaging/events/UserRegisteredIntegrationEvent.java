package pe.edu.upc.medibridge.payments.infrastructure.messaging.events;

import java.time.Instant;

public record UserRegisteredIntegrationEvent(
        Long userId,
        String username,
        Instant occurredAt,
        int version
) {
}
