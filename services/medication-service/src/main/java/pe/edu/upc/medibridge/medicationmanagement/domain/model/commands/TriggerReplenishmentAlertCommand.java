package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

public record TriggerReplenishmentAlertCommand(Integer medicationId, Long patientId, Integer currentStock) {
}
