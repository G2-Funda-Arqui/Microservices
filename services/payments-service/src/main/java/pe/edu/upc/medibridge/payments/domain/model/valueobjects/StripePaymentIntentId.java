package pe.edu.upc.medibridge.payments.domain.model.valueobjects;

public record StripePaymentIntentId(String value) {
    public StripePaymentIntentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Stripe payment intent id cannot be blank");
        }
    }
}
