package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.resources;

public record MedicationSummaryResponse(
        Long patientId,
        int activeMedications,
        int lowStockMedications,
        long doseAdministrations) {
}
