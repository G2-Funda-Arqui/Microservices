package pe.edu.upc.medibridge.iam.application.internal.commandservices;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.iam.domain.model.aggregates.User;
import pe.edu.upc.medibridge.iam.domain.model.commands.AuthenticateUserCommand;
import pe.edu.upc.medibridge.iam.domain.model.commands.SignInCommand;
import pe.edu.upc.medibridge.iam.domain.services.AuthenticationCommandService;
import pe.edu.upc.medibridge.iam.domain.services.UserCommandService;

import java.util.Optional;

@Service
public class AuthenticationCommandServiceImpl implements AuthenticationCommandService {
    private final UserCommandService userCommandService;

    public AuthenticationCommandServiceImpl(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(AuthenticateUserCommand command) {
        return userCommandService.handle(new SignInCommand(command.username(), command.password()));
    }
}
