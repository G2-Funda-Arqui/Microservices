package pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl;

import java.util.Optional;

public interface ExternalIamContextService {
    boolean userExists(Long userId);
    Optional<Long> findUserIdByUsername(String username);
}
