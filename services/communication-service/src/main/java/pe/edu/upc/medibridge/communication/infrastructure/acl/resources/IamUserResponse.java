package pe.edu.upc.medibridge.communication.infrastructure.acl.resources;

import java.util.List;

public record IamUserResponse(Long id, String username, List<String> roles) {
}
