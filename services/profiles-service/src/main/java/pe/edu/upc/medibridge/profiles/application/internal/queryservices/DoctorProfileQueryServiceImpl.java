package pe.edu.upc.medibridge.profiles.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorProfile;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetDoctorProfileByIdQuery;
import pe.edu.upc.medibridge.profiles.domain.services.DoctorProfileQueryService;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.DoctorProfileRepository;

import java.util.Optional;

@Service
public class DoctorProfileQueryServiceImpl implements DoctorProfileQueryService {

    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorProfileQueryServiceImpl(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Override
    public Optional<DoctorProfile> handle(GetDoctorProfileByIdQuery query) {
        return doctorProfileRepository.findById(query.doctorProfileId());
    }
}
