package pe.edu.upc.medibridge.payments.interfaces.rest.resources;

public record ProcessStripeWebhookRequest(String eventType, String stripePaymentIntentId, Long userId) {
}
