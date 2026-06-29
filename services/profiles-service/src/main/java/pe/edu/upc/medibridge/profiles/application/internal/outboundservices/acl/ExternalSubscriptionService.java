package pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl;

import java.util.Optional;

public interface ExternalSubscriptionService {
    Optional<ActiveSubscription> findActiveSubscriptionByUserId(Long userId);

    record ActiveSubscription(
            Long userId,
            String commercialLine,
            String planType,
            Integer maxPatients) {
    }
}
