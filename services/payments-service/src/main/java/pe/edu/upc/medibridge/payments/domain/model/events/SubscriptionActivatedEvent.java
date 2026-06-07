package pe.edu.upc.medibridge.payments.domain.model.events;

import java.time.Instant;

public record SubscriptionActivatedEvent(Long subscriptionId, Long userId, Instant occurredAt, int version) {
    public SubscriptionActivatedEvent(Long subscriptionId, Long userId) {
        this(subscriptionId, userId, Instant.now(), 1);
    }
}
