package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

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
    public boolean patientExists(Long patientId) {
        return patientId != null && profilesServiceClient.patientExists(patientId);
    }

    @Override
    public Optional<String> getPatientFullName(Long patientId) {
        if (patientId == null) {
            return Optional.empty();
        }
        var profile = profilesServiceClient.getPatientProfileById(patientId);
        return Optional.ofNullable(profile.fullName());
    }
}
