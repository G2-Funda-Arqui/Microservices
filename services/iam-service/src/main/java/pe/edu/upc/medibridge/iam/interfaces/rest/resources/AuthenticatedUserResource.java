package pe.edu.upc.medibridge.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(Long id, String username, String token) {
}
