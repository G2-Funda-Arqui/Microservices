package pe.edu.upc.medibridge.payments.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.payments.domain.model.commands.AddPaymentMethodCommand;
import pe.edu.upc.medibridge.payments.domain.model.entities.PaymentMethod;
import pe.edu.upc.medibridge.payments.domain.services.PaymentMethodCommandService;
import pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories.PaymentMethodRepository;

import java.util.Optional;

@Service
public class PaymentMethodCommandServiceImpl implements PaymentMethodCommandService {
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodCommandServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public Optional<PaymentMethod> handle(AddPaymentMethodCommand command) {
        return Optional.of(paymentMethodRepository.save(new PaymentMethod(command)));
    }
}
