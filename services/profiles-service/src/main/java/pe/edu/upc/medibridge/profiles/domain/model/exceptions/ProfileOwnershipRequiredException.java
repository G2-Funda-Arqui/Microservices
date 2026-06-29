package pe.edu.upc.medibridge.profiles.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ProfileOwnershipRequiredException extends RuntimeException {
    public ProfileOwnershipRequiredException(String message) {
        super(message);
    }
}
