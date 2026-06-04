package pe.edu.upc.medibridge.iam.domain.model.commands;

import java.util.List;

public record RegisterUserCommand(String username, String password, List<String> roles) {
}
