package pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl;

import java.util.Optional;

public interface ExternalIamContextService {
    Optional<Long> findUserIdByUsername(String username);
}
