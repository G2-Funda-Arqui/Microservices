package pe.edu.upc.medibridge.iam.domain.services;

import pe.edu.upc.medibridge.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
