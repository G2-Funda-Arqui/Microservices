package pe.edu.upc.medibridge.profiles.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetPatientProfileByIdQuery;
import pe.edu.upc.medibridge.profiles.domain.services.PatientProfileQueryService;

import java.util.Optional;

@Service
public class PatientProfileContextFacade {
    private final PatientProfileQueryService patientProfileQueryService;

    public PatientProfileContextFacade(PatientProfileQueryService patientProfileQueryService) {
        this.patientProfileQueryService = patientProfileQueryService;
    }

    public boolean patientExists(Long patientId) {
        return patientId != null && patientProfileQueryService
                .handle(new GetPatientProfileByIdQuery(patientId))
                .isPresent();
    }

    public Optional<String> fetchPatientFullNameById(Long patientId) {
        if (patientId == null) {
            return Optional.empty();
        }
        return patientProfileQueryService
                .handle(new GetPatientProfileByIdQuery(patientId))
                .map(patientProfile -> patientProfile.getFullName());
    }
}
