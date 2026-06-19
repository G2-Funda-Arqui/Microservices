package pe.edu.upc.medibridge.communication.interfaces.rest.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.communication.domain.model.documents.ChatMessage;
import pe.edu.upc.medibridge.communication.domain.services.ChatMessageService;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.ChatMessageResource;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.SendChatMessageResource;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/chat/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {
    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatMessageResource> sendMessage(@Valid @RequestBody SendChatMessageResource resource) {
        var saved = chatMessageService.save(ChatMessage.builder()
                .senderUserId(resource.senderUserId())
                .recipientUserId(resource.recipientUserId())
                .content(resource.content())
                .sentAt(resource.sentAt())
                .build());
        return new ResponseEntity<>(ChatMessageResource.from(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{senderUserId}/{recipientUserId}")
    public ResponseEntity<List<ChatMessageResource>> findMessages(
            @PathVariable Long senderUserId,
            @PathVariable Long recipientUserId) {
        var messages = chatMessageService.findChatMessages(senderUserId, recipientUserId)
                .stream()
                .map(ChatMessageResource::from)
                .toList();
        return ResponseEntity.ok(messages);
    }
}
