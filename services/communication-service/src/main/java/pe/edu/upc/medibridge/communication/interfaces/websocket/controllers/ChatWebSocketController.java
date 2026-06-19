package pe.edu.upc.medibridge.communication.interfaces.websocket.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import pe.edu.upc.medibridge.communication.domain.model.documents.ChatMessage;
import pe.edu.upc.medibridge.communication.domain.services.ChatMessageService;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.SendChatMessageResource;

@Controller
public class ChatWebSocketController {
    private final ChatMessageService chatMessageService;

    public ChatWebSocketController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload SendChatMessageResource resource) {
        chatMessageService.save(ChatMessage.builder()
                .senderUserId(resource.senderUserId())
                .recipientUserId(resource.recipientUserId())
                .content(resource.content())
                .sentAt(resource.sentAt())
                .build());
    }
}
