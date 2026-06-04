package pe.edu.upc.medibridge.profiles.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidUserReferenceException extends RuntimeException {
    public InvalidUserReferenceException(Long userId) {
        super("User reference not found: " + userId);
    }
}
