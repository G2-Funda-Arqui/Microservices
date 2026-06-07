package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.LowStockAlertResponse;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationResponse;

public class MedicationResponseFromEntityAssembler {
    public static MedicationResponse toResourceFromEntity(Medication entity) {
        return new MedicationResponse(
                entity.getId(),
                entity.getPatientId(),
                entity.getName(),
                entity.getDosageAmount(),
                entity.getDosageUnit(),
                entity.getAdministrationRoute(),
                entity.getStockQuantity(),
                entity.getLowStockThreshold(),
                entity.getExpirationDate(),
                entity.isActive());
    }

    public static LowStockAlertResponse toLowStockResourceFromEntity(Medication entity) {
        return new LowStockAlertResponse(
                entity.getId(),
                entity.getPatientId(),
                entity.getName(),
                entity.getStockQuantity(),
                entity.getLowStockThreshold());
    }
}
