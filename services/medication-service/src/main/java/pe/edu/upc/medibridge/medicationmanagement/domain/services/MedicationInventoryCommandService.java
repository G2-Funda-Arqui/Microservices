package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RegisterMedicationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.UpdateMedicationStockCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;

import java.util.Optional;

public interface MedicationInventoryCommandService {
    Optional<Medication> handle(RegisterMedicationCommand command);
    Optional<Medication> handle(UpdateMedicationStockCommand command);
}
