package pe.edu.upc.medibridge.payments.interfaces.rest.resources;

public record PaymentMethodResponse(Integer id, Long userId, String brand, String lastFourDigits, String stripePaymentMethodId) {
}
