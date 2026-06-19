package pe.edu.upc.medibridge.communication.interfaces.rest.resources;

import pe.edu.upc.medibridge.communication.domain.model.documents.ConnectedUser;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.UserConnectionStatus;

import java.time.Instant;

public record ConnectedUserResource(
        String id,
        Long userId,
        String username,
        String fullName,
        UserConnectionStatus status,
        Instant connectedAt,
        Instant disconnectedAt) {

    public static ConnectedUserResource from(ConnectedUser user) {
        return new ConnectedUserResource(
                user.getId(),
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getStatus(),
                user.getConnectedAt(),
                user.getDisconnectedAt());
    }
}
