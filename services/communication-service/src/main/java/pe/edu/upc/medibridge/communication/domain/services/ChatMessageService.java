package pe.edu.upc.medibridge.communication.domain.services;

import pe.edu.upc.medibridge.communication.domain.model.documents.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);
    List<ChatMessage> findChatMessages(Long senderUserId, Long recipientUserId);
}
