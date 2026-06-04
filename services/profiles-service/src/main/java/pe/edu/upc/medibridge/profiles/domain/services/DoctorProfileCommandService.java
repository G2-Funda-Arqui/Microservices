package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorProfile;
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateDoctorProfileCommand;

import java.util.Optional;

public interface DoctorProfileCommandService {
    Optional<DoctorProfile> handle(CreateDoctorProfileCommand command);
}
