package pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions;

public class InvalidHealthObservationException extends RuntimeException {
    public InvalidHealthObservationException(String message) {
        super(message);
    }
}
