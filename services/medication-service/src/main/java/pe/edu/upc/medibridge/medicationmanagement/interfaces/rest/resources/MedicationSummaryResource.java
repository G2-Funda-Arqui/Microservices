package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

public record MedicationSummaryResource(
        Long patientId,
        int activeMedications,
        int lowStockMedications,
        long doseAdministrations) {
}
