package pe.edu.upc.medibridge.communication.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.communication.domain.model.documents.ConnectedUser;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.UserConnectionStatus;
import pe.edu.upc.medibridge.communication.domain.services.ConnectedUserService;
import pe.edu.upc.medibridge.communication.infrastructure.persistence.mongodb.repositories.ConnectedUserRepository;

import java.time.Instant;
import java.util.List;

@Service
public class ConnectedUserServiceImpl implements ConnectedUserService {
    private final ConnectedUserRepository connectedUserRepository;

    public ConnectedUserServiceImpl(ConnectedUserRepository connectedUserRepository) {
        this.connectedUserRepository = connectedUserRepository;
    }

    @Override
    public ConnectedUser connect(ConnectedUser user) {
        var stored = connectedUserRepository.findByUserId(user.getUserId()).orElse(user);
        stored.setUserId(user.getUserId());
        stored.setUsername(user.getUsername());
        stored.setFullName(user.getFullName());
        stored.setStatus(UserConnectionStatus.ONLINE);
        stored.setConnectedAt(Instant.now());
        stored.setDisconnectedAt(null);
        return connectedUserRepository.save(stored);
    }

    @Override
    public ConnectedUser disconnect(Long userId) {
        var stored = connectedUserRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Connected user not found: " + userId));
        stored.setStatus(UserConnectionStatus.OFFLINE);
        stored.setDisconnectedAt(Instant.now());
        return connectedUserRepository.save(stored);
    }

    @Override
    public List<ConnectedUser> findConnectedUsers() {
        return connectedUserRepository.findAllByStatus(UserConnectionStatus.ONLINE);
    }
}
