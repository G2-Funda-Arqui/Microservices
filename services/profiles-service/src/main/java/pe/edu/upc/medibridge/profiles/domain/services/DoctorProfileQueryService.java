package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorProfile;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetDoctorProfileByIdQuery;

import java.util.Optional;

public interface DoctorProfileQueryService {
    Optional<DoctorProfile> handle(GetDoctorProfileByIdQuery query);
}
