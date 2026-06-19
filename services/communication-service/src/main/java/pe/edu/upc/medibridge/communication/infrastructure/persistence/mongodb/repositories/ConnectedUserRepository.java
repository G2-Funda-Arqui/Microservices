package pe.edu.upc.medibridge.communication.infrastructure.persistence.mongodb.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.upc.medibridge.communication.domain.model.documents.ConnectedUser;
import pe.edu.upc.medibridge.communication.domain.model.valueobjects.UserConnectionStatus;

import java.util.List;
import java.util.Optional;

public interface ConnectedUserRepository extends MongoRepository<ConnectedUser, String> {
    Optional<ConnectedUser> findByUserId(Long userId);
    List<ConnectedUser> findAllByStatus(UserConnectionStatus status);
}
