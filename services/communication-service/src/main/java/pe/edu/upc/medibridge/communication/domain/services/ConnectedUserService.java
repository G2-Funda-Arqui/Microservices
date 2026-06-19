package pe.edu.upc.medibridge.communication.domain.services;

import pe.edu.upc.medibridge.communication.domain.model.documents.ConnectedUser;

import java.util.List;

public interface ConnectedUserService {
    ConnectedUser connect(ConnectedUser user);
    ConnectedUser disconnect(Long userId);
    List<ConnectedUser> findConnectedUsers();
}
