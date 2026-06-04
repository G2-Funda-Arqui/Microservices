package pe.edu.upc.medibridge.iam.infrastructure.messaging.events;

import java.time.Instant;

public record SubscriptionActivatedIntegrationEvent(
        Long userId,
        Long subscriptionId,
        String status,
        Instant occurredAt,
        int version
) {
}
