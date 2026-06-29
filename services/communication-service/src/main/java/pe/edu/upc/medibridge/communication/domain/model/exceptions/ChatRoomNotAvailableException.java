package pe.edu.upc.medibridge.communication.domain.model.exceptions;

public class ChatRoomNotAvailableException extends RuntimeException {
    public ChatRoomNotAvailableException(Long senderUserId, Long recipientUserId) {
        super("Chat room is not available for users " + senderUserId + " and " + recipientUserId);
    }
}
