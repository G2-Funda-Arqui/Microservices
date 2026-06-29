package pe.edu.upc.medibridge.iam.domain.model.exceptions;

public class IntegrationEventPublishingException extends RuntimeException {
    public IntegrationEventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
