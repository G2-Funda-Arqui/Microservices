package pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PatientAccessDeniedException extends RuntimeException {
    public PatientAccessDeniedException(String message) {
        super(message);
    }
}
