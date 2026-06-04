package pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl;

public interface ExternalIamContextService {
    boolean userExists(Long userId);
}
