package pe.edu.upc.medibridge.appointments.infrastructure.acl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl.ExternalPatientAccessService;

@Service
public class PatientAccessServiceAdapter implements ExternalPatientAccessService {
    private final ProfilesServiceClient profilesServiceClient;

    public PatientAccessServiceAdapter(ProfilesServiceClient profilesServiceClient) {
        this.profilesServiceClient = profilesServiceClient;
    }

    @Override
    @CircuitBreaker(name = "profilesService", fallbackMethod = "canUserAccessPatientFallback")
    public boolean canUserAccessPatient(Long userId, Long patientId) {
        try {
            return profilesServiceClient.canUserAccessPatient(userId, patientId);
        } catch (FeignException exception) {
            return false;
        }
    }

    private boolean canUserAccessPatientFallback(Long userId, Long patientId, Throwable exception) {
        return false;
    }
}
