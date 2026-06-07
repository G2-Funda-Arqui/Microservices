package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class MedicationNotFoundException extends RuntimeException {
    public MedicationNotFoundException(Integer medicationId) {
        super("Medication not found with id: " + medicationId);
    }
}
