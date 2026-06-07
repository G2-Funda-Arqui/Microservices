package pe.edu.upc.medibridge.payments.interfaces.rest.transform;

import pe.edu.upc.medibridge.payments.domain.model.commands.AddPaymentMethodCommand;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.AddPaymentMethodRequest;

public class AddPaymentMethodCommandFromResourceAssembler {
    public static AddPaymentMethodCommand toCommandFromResource(AddPaymentMethodRequest resource) {
        return new AddPaymentMethodCommand(
                resource.userId(),
                resource.brand(),
                resource.lastFourDigits(),
                resource.stripePaymentMethodId());
    }
}
