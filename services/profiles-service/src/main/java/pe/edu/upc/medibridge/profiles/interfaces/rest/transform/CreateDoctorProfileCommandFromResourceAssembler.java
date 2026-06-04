package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateDoctorProfileCommand;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.CreateDoctorProfileResource;

public class CreateDoctorProfileCommandFromResourceAssembler {
    public static CreateDoctorProfileCommand toCommandFromResource(CreateDoctorProfileResource resource) {
        return new CreateDoctorProfileCommand(resource.userId(), resource.fullName());
    }
}
