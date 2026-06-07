package pe.edu.upc.medibridge.payments.interfaces.rest.transform;

import pe.edu.upc.medibridge.payments.domain.model.entities.PaymentMethod;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.PaymentMethodResponse;

public class PaymentMethodResponseFromEntityAssembler {
    public static PaymentMethodResponse toResourceFromEntity(PaymentMethod entity) {
        return new PaymentMethodResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getBrand(),
                entity.getLastFourDigits(),
                entity.getStripePaymentMethodId());
    }
}
