package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.CreateMedicationScheduleCommand;

import java.util.Optional;

public interface MedicationScheduleCommandService {
    Optional<MedicationSchedule> handle(CreateMedicationScheduleCommand command);
}
