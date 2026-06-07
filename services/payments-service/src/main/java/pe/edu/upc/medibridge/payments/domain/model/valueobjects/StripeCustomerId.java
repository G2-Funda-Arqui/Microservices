package pe.edu.upc.medibridge.payments.domain.model.valueobjects;

public record StripeCustomerId(String value) {
    public StripeCustomerId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Stripe customer id cannot be blank");
        }
    }
}
