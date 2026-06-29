package pe.edu.upc.medibridge.medicationmanagement.infrastructure.acl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientProfileReferenceAdapter {
    private final ProfilesServiceClient profilesServiceClient;

    public PatientProfileReferenceAdapter(ProfilesServiceClient profilesServiceClient) {
        this.profilesServiceClient = profilesServiceClient;
    }

    @CircuitBreaker(name = "profilesService", fallbackMethod = "patientExistsFallback")
    public boolean patientExists(Long patientId) {
        return profilesServiceClient.patientExists(patientId);
    }

    @CircuitBreaker(name = "profilesService", fallbackMethod = "getPatientFullNameFallback")
    public Optional<String> getPatientFullName(Long patientId) {
        var profile = profilesServiceClient.getPatientProfileById(patientId);
        return profile == null ? Optional.empty() : Optional.ofNullable(profile.fullName());
    }

    private boolean patientExistsFallback(Long patientId, Throwable exception) {
        return false;
    }

    private Optional<String> getPatientFullNameFallback(Long patientId, Throwable exception) {
        return Optional.empty();
    }
}
