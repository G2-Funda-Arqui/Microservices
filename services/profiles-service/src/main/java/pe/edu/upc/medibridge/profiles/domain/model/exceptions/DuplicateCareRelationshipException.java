package pe.edu.upc.medibridge.profiles.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateCareRelationshipException extends RuntimeException {
    public DuplicateCareRelationshipException(String message) {
        super(message);
    }
}
