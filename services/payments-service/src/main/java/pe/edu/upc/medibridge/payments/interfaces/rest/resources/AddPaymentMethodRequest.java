package pe.edu.upc.medibridge.payments.interfaces.rest.resources;

public record AddPaymentMethodRequest(Long userId, String brand, String lastFourDigits, String stripePaymentMethodId) {
}
