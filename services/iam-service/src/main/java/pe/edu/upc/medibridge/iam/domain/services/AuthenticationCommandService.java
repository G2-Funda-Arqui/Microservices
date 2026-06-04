package pe.edu.upc.medibridge.iam.domain.services;

import org.apache.commons.lang3.tuple.ImmutablePair;
import pe.edu.upc.medibridge.iam.domain.model.aggregates.User;
import pe.edu.upc.medibridge.iam.domain.model.commands.AuthenticateUserCommand;

import java.util.Optional;

public interface AuthenticationCommandService {
    Optional<ImmutablePair<User, String>> handle(AuthenticateUserCommand command);
}
