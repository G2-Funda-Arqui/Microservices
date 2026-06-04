package pe.edu.upc.medibridge.iam.infrastructure.persistence.jpa.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.iam.domain.model.commands.SeedRolesCommand;
import pe.edu.upc.medibridge.iam.domain.services.RoleCommandService;

@Component
public class IamDataInitializer implements CommandLineRunner {
    private final RoleCommandService roleCommandService;

    public IamDataInitializer(RoleCommandService roleCommandService) {
        this.roleCommandService = roleCommandService;
    }

    @Override
    public void run(String... args) {
        roleCommandService.handle(new SeedRolesCommand());
    }
}
