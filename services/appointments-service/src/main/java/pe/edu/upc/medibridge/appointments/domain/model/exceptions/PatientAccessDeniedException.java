package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PatientAccessDeniedException extends RuntimeException {
    public PatientAccessDeniedException(String message) {
        super(message);
    }
}
