package pe.edu.upc.medibridge.payments.domain.model.events;

import java.time.Instant;

public record PaymentFailedEvent(Long userId, String stripePaymentIntentId, Instant occurredAt, int version) {
    public PaymentFailedEvent(Long userId, String stripePaymentIntentId) {
        this(userId, stripePaymentIntentId, Instant.now(), 1);
    }
}
