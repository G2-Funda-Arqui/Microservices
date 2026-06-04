package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.PatientProfile;
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreatePatientProfileCommand;

import java.util.Optional;

public interface PatientProfileCommandService {
    Optional<PatientProfile> handle(CreatePatientProfileCommand command);
}
