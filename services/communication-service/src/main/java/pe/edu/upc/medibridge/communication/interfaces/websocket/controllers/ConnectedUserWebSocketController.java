package pe.edu.upc.medibridge.communication.interfaces.websocket.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import pe.edu.upc.medibridge.communication.domain.model.documents.ConnectedUser;
import pe.edu.upc.medibridge.communication.domain.services.ConnectedUserService;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.ConnectUserResource;
import pe.edu.upc.medibridge.communication.interfaces.rest.resources.ConnectedUserResource;

@Controller
public class ConnectedUserWebSocketController {
    private final ConnectedUserService connectedUserService;

    public ConnectedUserWebSocketController(ConnectedUserService connectedUserService) {
        this.connectedUserService = connectedUserService;
    }

    @MessageMapping("/user.addUser")
    @SendTo("/topic/users")
    public ConnectedUserResource addUser(@Payload ConnectUserResource resource) {
        var user = connectedUserService.connect(ConnectedUser.builder()
                .userId(resource.userId())
                .username(resource.username())
                .fullName(resource.fullName())
                .build());
        return ConnectedUserResource.from(user);
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/topic/users")
    public ConnectedUserResource disconnectUser(@Payload ConnectUserResource resource) {
        return ConnectedUserResource.from(connectedUserService.disconnect(resource.userId()));
    }
}
