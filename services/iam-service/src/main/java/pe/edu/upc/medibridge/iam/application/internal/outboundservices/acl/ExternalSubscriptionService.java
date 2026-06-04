package pe.edu.upc.medibridge.iam.application.internal.outboundservices.acl;

public interface ExternalSubscriptionService {
    boolean hasActiveSubscription(Long userId);
}
