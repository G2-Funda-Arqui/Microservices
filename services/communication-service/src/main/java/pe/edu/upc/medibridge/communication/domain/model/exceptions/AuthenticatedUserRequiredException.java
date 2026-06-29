package pe.edu.upc.medibridge.communication.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticatedUserRequiredException extends RuntimeException {
    public AuthenticatedUserRequiredException(String message) {
        super(message);
    }
}
