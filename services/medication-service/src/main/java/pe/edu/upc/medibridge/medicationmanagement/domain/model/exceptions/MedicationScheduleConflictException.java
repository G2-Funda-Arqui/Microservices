package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class MedicationScheduleConflictException extends RuntimeException {
    public MedicationScheduleConflictException(Integer medicationId) {
        super("Medication schedule conflict for medication: " + medicationId);
    }
}
