package pe.edu.upc.medibridge.payments.domain.services;

import pe.edu.upc.medibridge.payments.domain.model.commands.AddPaymentMethodCommand;
import pe.edu.upc.medibridge.payments.domain.model.entities.PaymentMethod;

import java.util.Optional;

public interface PaymentMethodCommandService {
    Optional<PaymentMethod> handle(AddPaymentMethodCommand command);
}
