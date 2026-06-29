package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RegisterMedicationCommand;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.RegisterMedicationRequest;

public class RegisterMedicationCommandFromResourceAssembler {
    public static RegisterMedicationCommand toCommandFromResource(RegisterMedicationRequest resource, Long requestedByUserId) {
        return new RegisterMedicationCommand(
                resource.patientId(),
                resource.name(),
                resource.dosageAmount(),
                resource.dosageUnit(),
                resource.administrationRoute(),
                resource.stockQuantity(),
                resource.lowStockThreshold(),
                resource.expirationDate(),
                requestedByUserId);
    }
}
