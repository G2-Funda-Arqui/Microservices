package pe.edu.upc.medibridge.healthmonitoring.application.internal.outboundservices.acl;

import java.util.Optional;

public interface ExternalSubscriptionService {
    Optional<ActiveSubscription> findActiveSubscriptionByUserId(Long userId);

    record ActiveSubscription(Long userId, String commercialLine, String planType, Integer maxPatients) {
        public boolean isPaidPlan() {
            return planType != null && !"FREE".equals(planType);
        }
    }
}
