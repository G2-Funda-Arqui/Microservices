package pe.edu.upc.medibridge.communication.domain.model.exceptions;

public class ChatAccessDeniedException extends RuntimeException {
    public ChatAccessDeniedException(String message) {
        super(message);
    }
}
