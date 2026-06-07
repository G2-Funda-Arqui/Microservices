package pe.edu.upc.medibridge.payments.domain.model.exceptions;

public class SubscriptionAlreadyActiveException extends RuntimeException {
    public SubscriptionAlreadyActiveException(Long userId) {
        super("User already has an active subscription: " + userId);
    }
}
