package pe.edu.upc.medibridge.communication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record SendChatMessageResource(
        @NotNull Long senderUserId,
        @NotNull Long recipientUserId,
        @NotBlank String content,
        Instant sentAt) {
}
