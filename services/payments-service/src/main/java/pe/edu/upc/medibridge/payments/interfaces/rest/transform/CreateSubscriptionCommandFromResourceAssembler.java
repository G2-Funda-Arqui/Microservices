package pe.edu.upc.medibridge.payments.interfaces.rest.transform;

import pe.edu.upc.medibridge.payments.domain.model.commands.CreateSubscriptionCommand;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.CreateSubscriptionRequest;

public class CreateSubscriptionCommandFromResourceAssembler {
    public static CreateSubscriptionCommand toCommandFromResource(CreateSubscriptionRequest resource) {
        return new CreateSubscriptionCommand(
                resource.userId(),
                resource.commercialLine(),
                resource.planType(),
                resource.billingCycle());
    }
}
