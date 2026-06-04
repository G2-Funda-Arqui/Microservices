package pe.edu.upc.medibridge.iam.domain.model.commands;

public record AuthenticateUserCommand(String username, String password) {
}
