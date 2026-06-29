package pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PremiumSubscriptionRequiredException extends RuntimeException {
    public PremiumSubscriptionRequiredException(String message) {
        super(message);
    }
}
