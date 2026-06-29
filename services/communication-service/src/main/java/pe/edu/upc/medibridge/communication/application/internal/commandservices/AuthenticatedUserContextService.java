package pe.edu.upc.medibridge.communication.application.internal.commandservices;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.communication.application.internal.outboundservices.acl.ExternalIamContextService;
import pe.edu.upc.medibridge.communication.domain.model.exceptions.AuthenticatedUserRequiredException;

@Service
public class AuthenticatedUserContextService {
    private final ExternalIamContextService externalIamContextService;

    public AuthenticatedUserContextService(ExternalIamContextService externalIamContextService) {
        this.externalIamContextService = externalIamContextService;
    }

    public Long resolveAuthenticatedUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new AuthenticatedUserRequiredException("Authenticated user is required");
        }
        return resolveAuthenticatedUserId(jwt.getSubject());
    }

    public Long resolveAuthenticatedUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new AuthenticatedUserRequiredException("Authenticated user is required");
        }
        return externalIamContextService.findUserIdByUsername(username)
                .orElseThrow(() -> new AuthenticatedUserRequiredException("Authenticated user was not found"));
    }
}
