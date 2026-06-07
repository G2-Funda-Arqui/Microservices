package pe.edu.upc.medibridge.payments.application.internal.outboundservices.acl;

public interface ExternalIamSubscriptionService {
    boolean userExists(Long userId);
}
