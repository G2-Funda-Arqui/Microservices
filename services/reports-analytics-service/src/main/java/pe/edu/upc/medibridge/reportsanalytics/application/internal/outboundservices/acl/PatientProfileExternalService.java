package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.ProfilesServiceClient;

import java.util.Optional;

@Service
public class PatientProfileExternalService implements ExternalPatientProfileService {
    private final ProfilesServiceClient profilesServiceClient;

    public PatientProfileExternalService(ProfilesServiceClient profilesServiceClient) {
        this.profilesServiceClient = profilesServiceClient;
    }

    @Override
    @CircuitBreaker(name = "profilesService", fallbackMethod = "patientExistsFallback")
    public boolean patientExists(Long patientId) {
        return patientId != null && profilesServiceClient.patientExists(patientId);
    }

    @Override
    @CircuitBreaker(name = "profilesService", fallbackMethod = "getPatientFullNameFallback")
    public Optional<String> getPatientFullName(Long patientId) {
        if (patientId == null) {
            return Optional.empty();
        }
        var profile = profilesServiceClient.getPatientProfileById(patientId);
        return Optional.ofNullable(profile.fullName());
    }

    private boolean patientExistsFallback(Long patientId, Throwable exception) {
        return false;
    }

    private Optional<String> getPatientFullNameFallback(Long patientId, Throwable exception) {
        return Optional.empty();
    }
}
