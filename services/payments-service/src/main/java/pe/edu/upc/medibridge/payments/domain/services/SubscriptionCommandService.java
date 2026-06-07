package pe.edu.upc.medibridge.payments.domain.services;

import pe.edu.upc.medibridge.payments.domain.model.aggregates.Subscription;
import pe.edu.upc.medibridge.payments.domain.model.commands.CancelSubscriptionCommand;
import pe.edu.upc.medibridge.payments.domain.model.commands.CreateSubscriptionCommand;
import pe.edu.upc.medibridge.payments.domain.model.commands.RenewSubscriptionCommand;

import java.util.Optional;

public interface SubscriptionCommandService {
    Optional<Subscription> handle(CreateSubscriptionCommand command);
    Optional<Subscription> handle(CancelSubscriptionCommand command);
    Optional<Subscription> handle(RenewSubscriptionCommand command);
}
