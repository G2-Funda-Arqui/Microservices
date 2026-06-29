package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateFamilyMemberProfileCommand;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.CreateFamilyMemberProfileResource;

public class CreateFamilyMemberProfileCommandFromResourceAssembler {
    public static CreateFamilyMemberProfileCommand toCommandFromResource(
            CreateFamilyMemberProfileResource resource,
            Long userId) {
        return new CreateFamilyMemberProfileCommand(userId, resource.fullName());
    }
}
