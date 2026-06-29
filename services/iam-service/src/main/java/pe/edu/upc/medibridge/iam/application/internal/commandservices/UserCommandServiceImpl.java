package pe.edu.upc.medibridge.iam.application.internal.commandservices;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.medibridge.iam.domain.model.aggregates.User;
import pe.edu.upc.medibridge.iam.domain.model.commands.SignInCommand;
import pe.edu.upc.medibridge.iam.domain.model.commands.SignUpCommand;
import pe.edu.upc.medibridge.iam.domain.model.entities.Role;
import pe.edu.upc.medibridge.iam.domain.model.exceptions.InvalidCredentialsException;
import pe.edu.upc.medibridge.iam.domain.model.exceptions.RoleNotFoundException;
import pe.edu.upc.medibridge.iam.domain.model.exceptions.UserNotFoundException;
import pe.edu.upc.medibridge.iam.domain.model.exceptions.UsernameAlreadyExistsException;
import pe.edu.upc.medibridge.iam.domain.model.valueobjects.Roles;
import pe.edu.upc.medibridge.iam.domain.services.HashingService;
import pe.edu.upc.medibridge.iam.domain.services.TokenService;
import pe.edu.upc.medibridge.iam.domain.services.UserCommandService;
import pe.edu.upc.medibridge.iam.infrastructure.messaging.publishers.UserIntegrationEventPublisher;
import pe.edu.upc.medibridge.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import pe.edu.upc.medibridge.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final UserIntegrationEventPublisher integrationEventPublisher;

    public UserCommandServiceImpl(UserRepository userRepository,
                                  HashingService hashingService,
                                  TokenService tokenService,
                                  RoleRepository roleRepository,
                                  UserIntegrationEventPublisher integrationEventPublisher) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var userOpt = userRepository.findByUsername(command.username());
        if (userOpt.isEmpty())
            throw new UserNotFoundException(command.username());
        var user = userOpt.get();
        if (!hashingService.matches(command.password(), user.getPassword()))
            throw new InvalidCredentialsException();

        var token = tokenService.generateToken(user.getUsername());
        return Optional.of(ImmutablePair.of(user, token));
    }

    @Override
    @Transactional
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username()))
            throw new UsernameAlreadyExistsException(command.username());

        Set<Role> rolesToAssign = new HashSet<>();
        if (command.roles() != null && !command.roles().isEmpty()) {
            for (var roleVO : command.roles()) {
                var roleEntity = roleRepository.findByName(roleVO.getName())
                        .orElseThrow(() -> new RoleNotFoundException(roleVO.getName().name()));
                rolesToAssign.add(roleEntity);
            }
        } else {
            var defaultRole = roleRepository.findByName(Roles.valueOf("ROLE_USER"))
                    .orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));
            rolesToAssign.add(defaultRole);
        }

        var user = new User(command.username(), hashingService.encode(command.password()));
        user.setRoles(rolesToAssign);

        var savedUser = userRepository.save(user);
        integrationEventPublisher.publishUserRegistered(savedUser.getId(), savedUser.getUsername());

        return userRepository.findByUsername(command.username());
    }
}
