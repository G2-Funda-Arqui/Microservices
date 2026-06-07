package pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions;

public class InvalidPatientReferenceException extends RuntimeException {
    public InvalidPatientReferenceException(Long patientId) {
        super("Invalid patient reference: " + patientId);
    }
}
