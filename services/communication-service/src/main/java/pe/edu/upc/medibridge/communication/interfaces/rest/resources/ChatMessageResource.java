package pe.edu.upc.medibridge.communication.interfaces.rest.resources;

import pe.edu.upc.medibridge.communication.domain.model.documents.ChatMessage;

import java.time.Instant;

public record ChatMessageResource(
        String id,
        String chatId,
        Long senderUserId,
        Long recipientUserId,
        String content,
        Instant sentAt) {

    public static ChatMessageResource from(ChatMessage message) {
        return new ChatMessageResource(
                message.getId(),
                message.getChatId(),
                message.getSenderUserId(),
                message.getRecipientUserId(),
                message.getContent(),
                message.getSentAt());
    }
}
