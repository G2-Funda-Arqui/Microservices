package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

public record LowStockAlertResponse(Integer medicationId, Long patientId, String medicationName, Integer currentStock, Integer threshold) {
}
