package pe.edu.upc.medibridge.payments.domain.services;

import pe.edu.upc.medibridge.payments.domain.model.aggregates.Subscription;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetActiveSubscriptionQuery;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetSubscriptionByUserQuery;

import java.util.Optional;

public interface SubscriptionQueryService {
    Optional<Subscription> handle(GetSubscriptionByUserQuery query);
    Optional<Subscription> handle(GetActiveSubscriptionQuery query);
}
