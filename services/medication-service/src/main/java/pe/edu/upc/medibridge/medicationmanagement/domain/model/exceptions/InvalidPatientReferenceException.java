package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class InvalidPatientReferenceException extends RuntimeException {
    public InvalidPatientReferenceException(Long patientId) {
        super("Patient reference not found: " + patientId);
    }
}
