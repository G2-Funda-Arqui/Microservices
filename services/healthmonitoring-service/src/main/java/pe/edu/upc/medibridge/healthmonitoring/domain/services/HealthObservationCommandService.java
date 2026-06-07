package pe.edu.upc.medibridge.healthmonitoring.domain.services;

import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.PatientHealthObservation;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.commands.RecordPatientHealthObservationCommand;

import java.util.Optional;

public interface HealthObservationCommandService {
    Optional<PatientHealthObservation> handle(RecordPatientHealthObservationCommand command);
}
