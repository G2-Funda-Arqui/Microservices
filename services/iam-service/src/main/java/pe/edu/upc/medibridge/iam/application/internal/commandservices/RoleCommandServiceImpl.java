package pe.edu.upc.medibridge.iam.application.internal.commandservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.iam.domain.model.commands.SeedRolesCommand;
import pe.edu.upc.medibridge.iam.domain.model.entities.Role;
import pe.edu.upc.medibridge.iam.domain.model.valueobjects.Roles;
import pe.edu.upc.medibridge.iam.domain.services.RoleCommandService;
import pe.edu.upc.medibridge.iam.infrastructure.persistence.jpa.repositories.RoleRepository;

import java.util.Arrays;

/**
 * Implementation of {@link RoleCommandService} to handle {@link SeedRolesCommand}
 * Ensures that roles are created only if they do not exist yet.
 */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleCommandServiceImpl.class);

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Handle the {@link SeedRolesCommand} and create roles if they do not exist.
     * @param command the seed roles command
     */
    @Override
    public void handle(SeedRolesCommand command) {
        LOGGER.info("Executing role seeding...");
        Arrays.stream(Roles.values()).forEach(roleEnum -> {
            roleRepository.findByName(roleEnum)
                    .ifPresentOrElse(
                            existingRole -> LOGGER.info("Role already exists: {}", roleEnum.name()),
                            () -> {
                                LOGGER.info("Creating role: {}", roleEnum.name());
                                roleRepository.save(new Role(roleEnum));
                            }
                    );
        });
        LOGGER.info("Role seeding finished.");
    }
}
