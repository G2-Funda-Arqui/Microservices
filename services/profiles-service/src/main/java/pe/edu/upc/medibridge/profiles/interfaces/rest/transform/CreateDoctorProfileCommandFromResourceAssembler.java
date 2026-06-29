package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateDoctorProfileCommand;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.CreateDoctorProfileResource;

public class CreateDoctorProfileCommandFromResourceAssembler {
    public static CreateDoctorProfileCommand toCommandFromResource(CreateDoctorProfileResource resource, Long userId) {
        return new CreateDoctorProfileCommand(userId, resource.fullName());
    }
}
