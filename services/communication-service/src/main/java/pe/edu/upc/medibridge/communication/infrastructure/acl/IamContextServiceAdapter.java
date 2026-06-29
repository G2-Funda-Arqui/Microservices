package pe.edu.upc.medibridge.communication.infrastructure.acl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pe.edu.upc.medibridge.communication.application.internal.outboundservices.acl.ExternalIamContextService;
import pe.edu.upc.medibridge.communication.infrastructure.acl.resources.IamUserResponse;

import java.util.Optional;

@Service
public class IamContextServiceAdapter implements ExternalIamContextService {
    private final RestClient restClient;

    public IamContextServiceAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${medibridge.services.iam.base-url}") String iamServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(iamServiceBaseUrl).build();
    }

    @Override
    @CircuitBreaker(name = "iamService", fallbackMethod = "findUserIdByUsernameFallback")
    public Optional<Long> findUserIdByUsername(String username) {
        try {
            var user = restClient.get()
                    .uri("/api/v1/internal/users/by-username/{username}", username)
                    .retrieve()
                    .body(IamUserResponse.class);
            return user == null ? Optional.empty() : Optional.ofNullable(user.id());
        } catch (RestClientException exception) {
            return Optional.empty();
        }
    }

    private Optional<Long> findUserIdByUsernameFallback(String username, Throwable exception) {
        return Optional.empty();
    }
}
