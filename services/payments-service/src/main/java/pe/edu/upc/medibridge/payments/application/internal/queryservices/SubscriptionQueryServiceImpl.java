package pe.edu.upc.medibridge.payments.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.payments.domain.model.aggregates.Subscription;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetActiveSubscriptionQuery;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetSubscriptionByUserQuery;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.SubscriptionStatus;
import pe.edu.upc.medibridge.payments.domain.services.SubscriptionQueryService;
import pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories.SubscriptionRepository;

import java.util.Optional;

@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByUserQuery query) {
        return subscriptionRepository.findFirstByUserIdOrderByCreatedAtDesc(query.userId());
    }

    @Override
    public Optional<Subscription> handle(GetActiveSubscriptionQuery query) {
        return subscriptionRepository.findByUserIdAndStatus(query.userId(), SubscriptionStatus.ACTIVE);
    }
}
