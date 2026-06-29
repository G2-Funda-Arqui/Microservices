package pe.edu.upc.medibridge.communication.infrastructure.acl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Objects;

@Component
public class ProfilesServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilesServiceClient.class);

    private final RestClient restClient;

    public ProfilesServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${medibridge.services.profiles.base-url}") String profilesServiceBaseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(profilesServiceBaseUrl)
                .build();
    }

    @CircuitBreaker(name = "profilesService", fallbackMethod = "getCareTeamUserIdsFallback")
    public List<Long> getCareTeamUserIds(Long patientId) {
        try {
            var resource = restClient.get()
                    .uri("/api/v1/internal/profiles/patients/{patientId}/care-team-members", patientId)
                    .retrieve()
                    .body(CareTeamMembersResource.class);

            if (resource == null || resource.careTeamUserIds() == null) {
                return List.of();
            }

            return resource.careTeamUserIds()
                    .stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
        } catch (RestClientException exception) {
            LOGGER.warn("Could not resolve care team user ids for patientId={}. No notification recipients were created.",
                    patientId,
                    exception);
            return List.of();
        }
    }

    private List<Long> getCareTeamUserIdsFallback(Long patientId, Throwable exception) {
        LOGGER.warn("Profiles circuit breaker fallback while resolving care team user ids for patientId={}", patientId, exception);
        return List.of();
    }
}
