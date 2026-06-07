package pe.edu.upc.medibridge.payments.application.internal.outboundservices.acl;

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
    public boolean userExists(Long userId) {
        return userReferenceRepository.existsByUserId(userId) || iamServiceClient.userExists(userId);
    }
}
