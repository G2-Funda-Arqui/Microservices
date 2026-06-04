package pe.edu.upc.medibridge.profiles.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.PatientProfile;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetPatientProfileByIdQuery;
import pe.edu.upc.medibridge.profiles.domain.services.PatientProfileQueryService;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.PatientProfileRepository;

import java.util.Optional;

@Service
public class PatientProfileQueryServiceImpl implements PatientProfileQueryService {

    private final PatientProfileRepository patientProfileRepository;

    public PatientProfileQueryServiceImpl(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    @Override
    public Optional<PatientProfile> handle(GetPatientProfileByIdQuery query) {
        return patientProfileRepository.findById(query.patientId());
    }
}
