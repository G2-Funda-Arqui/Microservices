package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

public record UpdateMedicationStockCommand(Integer medicationId, Integer stockQuantity, Long requestedByUserId) {
}
