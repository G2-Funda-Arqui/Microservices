package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ProfileRelationshipNotAllowedException extends RuntimeException {
    public ProfileRelationshipNotAllowedException(String message) {
        super(message);
    }
}
