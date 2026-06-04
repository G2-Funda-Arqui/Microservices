package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.PatientProfile;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetPatientProfileByIdQuery;

import java.util.Optional;

public interface PatientProfileQueryService {
    Optional<PatientProfile> handle(GetPatientProfileByIdQuery query);
}
