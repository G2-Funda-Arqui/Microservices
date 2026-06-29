package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl.ExternalIamContextService;

import java.util.Optional;

@Service
public class IamContextServiceAdapter implements ExternalIamContextService {
    private final IamServiceClient iamServiceClient;

    public IamContextServiceAdapter(IamServiceClient iamServiceClient) {
        this.iamServiceClient = iamServiceClient;
    }

    @Override
    @CircuitBreaker(name = "iamService", fallbackMethod = "findUserIdByUsernameFallback")
    public Optional<Long> findUserIdByUsername(String username) {
        try {
            var user = iamServiceClient.getUserByUsername(username);
            return user == null ? Optional.empty() : Optional.ofNullable(user.id());
        } catch (FeignException.NotFound exception) {
            return Optional.empty();
        }
    }

    private Optional<Long> findUserIdByUsernameFallback(String username, Throwable exception) {
        return Optional.empty();
    }
}
