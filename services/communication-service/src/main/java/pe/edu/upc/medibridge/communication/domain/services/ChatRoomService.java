package pe.edu.upc.medibridge.communication.domain.services;

import java.util.Optional;

public interface ChatRoomService {
    Optional<String> getChatRoomId(Long senderUserId, Long recipientUserId, boolean createIfMissing);
}
