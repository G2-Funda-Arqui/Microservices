package pe.edu.upc.medibridge.profiles.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PatientLimitExceededException extends RuntimeException {
    public PatientLimitExceededException(String message) {
        super(message);
    }
}
