package pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidPatientReferenceException extends RuntimeException {
    public InvalidPatientReferenceException(Long patientId) {
        super("Invalid patient reference: " + patientId);
    }
}
