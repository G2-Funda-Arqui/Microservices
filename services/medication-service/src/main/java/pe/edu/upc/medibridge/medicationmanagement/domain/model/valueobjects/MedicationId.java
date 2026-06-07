package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

public record MedicationId(Integer value) {
    public MedicationId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Medication id must be a positive number");
        }
    }
}
