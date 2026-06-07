package pe.edu.upc.medibridge.payments.infrastructure.messaging.events;

import java.time.Instant;

public record SubscriptionActivatedIntegrationEvent(
        Long userId,
        Long subscriptionId,
        String status,
        Instant occurredAt,
        int version
) {
    public SubscriptionActivatedIntegrationEvent(Long userId, Long subscriptionId) {
        this(userId, subscriptionId, "ACTIVE", Instant.now(), 1);
    }
}
