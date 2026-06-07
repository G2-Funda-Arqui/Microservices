package pe.edu.upc.medibridge.payments.domain.model.commands;

public record ProcessStripeWebhookCommand(String eventType, String stripePaymentIntentId, Long userId) {
}
