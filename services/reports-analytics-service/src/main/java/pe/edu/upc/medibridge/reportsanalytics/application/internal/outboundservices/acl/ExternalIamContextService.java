package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import java.util.Optional;

public interface ExternalIamContextService {
    Optional<Long> findUserIdByUsername(String username);
}
