package pe.edu.upc.medibridge.payments.domain.model.commands;

public record AddPaymentMethodCommand(Long userId, String brand, String lastFourDigits, String stripePaymentMethodId) {
}
