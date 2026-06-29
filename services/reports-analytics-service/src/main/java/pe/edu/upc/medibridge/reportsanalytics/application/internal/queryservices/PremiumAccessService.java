package pe.edu.upc.medibridge.reportsanalytics.application.internal.queryservices;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl.ExternalIamContextService;
import pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl.ExternalSubscriptionService;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions.PremiumSubscriptionRequiredException;

@Service
public class PremiumAccessService {
    private final ExternalIamContextService externalIamContextService;
    private final ExternalSubscriptionService externalSubscriptionService;

    public PremiumAccessService(
            ExternalIamContextService externalIamContextService,
            ExternalSubscriptionService externalSubscriptionService) {
        this.externalIamContextService = externalIamContextService;
        this.externalSubscriptionService = externalSubscriptionService;
    }

    public void requirePaidSubscription(Jwt jwt, String featureName) {
        var userId = resolveAuthenticatedUserId(jwt);
        var hasPaidPlan = externalSubscriptionService.findActiveSubscriptionByUserId(userId)
                .map(ExternalSubscriptionService.ActiveSubscription::isPaidPlan)
                .orElse(false);
        if (!hasPaidPlan) {
            throw new PremiumSubscriptionRequiredException("A premium subscription is required to use " + featureName);
        }
    }

    private Long resolveAuthenticatedUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new PremiumSubscriptionRequiredException("Authenticated user is required");
        }
        return externalIamContextService.findUserIdByUsername(jwt.getSubject())
                .orElseThrow(() -> new PremiumSubscriptionRequiredException("Authenticated user was not found"));
    }
}
