package pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidHealthObservationException extends RuntimeException {
    public InvalidHealthObservationException(String message) {
        super(message);
    }
}
