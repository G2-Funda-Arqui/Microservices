package pe.edu.upc.medibridge.profiles.infrastructure.acl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl.ExternalSubscriptionService;

import java.util.Optional;

@Service
public class SubscriptionServiceAdapter implements ExternalSubscriptionService {
    private final PaymentsServiceClient paymentsServiceClient;

    public SubscriptionServiceAdapter(PaymentsServiceClient paymentsServiceClient) {
        this.paymentsServiceClient = paymentsServiceClient;
    }

    @Override
    @CircuitBreaker(name = "paymentsService", fallbackMethod = "findActiveSubscriptionByUserIdFallback")
    public Optional<ActiveSubscription> findActiveSubscriptionByUserId(Long userId) {
        try {
            var subscription = paymentsServiceClient.getActiveSubscriptionByUser(userId);
            if (subscription == null || subscription.plan() == null) {
                return Optional.empty();
            }
            var plan = subscription.plan();
            return Optional.of(new ActiveSubscription(
                    subscription.userId(),
                    plan.commercialLine(),
                    plan.planType(),
                    plan.maxPatients()));
        } catch (FeignException.NotFound exception) {
            return Optional.empty();
        }
    }

    private Optional<ActiveSubscription> findActiveSubscriptionByUserIdFallback(Long userId, Throwable exception) {
        return Optional.empty();
    }
}
