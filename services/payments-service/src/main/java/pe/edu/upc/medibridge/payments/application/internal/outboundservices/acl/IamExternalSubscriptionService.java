package pe.edu.upc.medibridge.payments.application.internal.outboundservices.acl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.payments.infrastructure.acl.IamServiceClient;
import pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories.UserReferenceRepository;

@Service
public class IamExternalSubscriptionService implements ExternalIamSubscriptionService {
    private final IamServiceClient iamServiceClient;
    private final UserReferenceRepository userReferenceRepository;

    public IamExternalSubscriptionService(
            IamServiceClient iamServiceClient,
            UserReferenceRepository userReferenceRepository) {
        this.iamServiceClient = iamServiceClient;
        this.userReferenceRepository = userReferenceRepository;
    }

    @Override
    @CircuitBreaker(name = "iamService", fallbackMethod = "userExistsFallback")
    public boolean userExists(Long userId) {
        return userReferenceRepository.existsByUserId(userId) || iamServiceClient.userExists(userId);
    }

    private boolean userExistsFallback(Long userId, Throwable exception) {
        return userReferenceRepository.existsByUserId(userId);
    }
}
