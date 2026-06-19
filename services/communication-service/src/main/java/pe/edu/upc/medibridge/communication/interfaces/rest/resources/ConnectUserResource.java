package pe.edu.upc.medibridge.communication.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record ConnectUserResource(
        @NotNull Long userId,
        String username,
        String fullName) {
}
