package pe.edu.upc.medibridge.payments.domain.model.exceptions;

public class InvalidPlanTransitionException extends RuntimeException {
    public InvalidPlanTransitionException(String message) {
        super(message);
    }
}
