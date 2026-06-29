package pe.edu.upc.medibridge.communication.interfaces.websocket.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import pe.edu.upc.medibridge.communication.application.internal.commandservices.AuthenticatedUserContextService;
import pe.edu.upc.medibridge.communication.domain.model.documents.ChatMessage;
import pe.edu.upc.medibridge.communication.domain.services.ChatMessageService;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.SendChatMessageResource;

import java.security.Principal;

@Controller
public class ChatWebSocketController {
    private final ChatMessageService chatMessageService;
    private final AuthenticatedUserContextService authenticatedUserContextService;

    public ChatWebSocketController(
            ChatMessageService chatMessageService,
            AuthenticatedUserContextService authenticatedUserContextService) {
        this.chatMessageService = chatMessageService;
        this.authenticatedUserContextService = authenticatedUserContextService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload SendChatMessageResource resource, Principal principal) {
        var senderUserId = authenticatedUserContextService.resolveAuthenticatedUserId(principal.getName());
        chatMessageService.save(ChatMessage.builder()
                .senderUserId(senderUserId)
                .recipientUserId(resource.recipientUserId())
                .content(resource.content())
                .sentAt(resource.sentAt())
                .build());
    }
}
