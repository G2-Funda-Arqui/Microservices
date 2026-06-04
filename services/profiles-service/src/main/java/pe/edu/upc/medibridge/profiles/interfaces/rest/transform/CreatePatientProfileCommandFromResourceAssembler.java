package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.commands.CreatePatientProfileCommand;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.CreatePatientProfileResource;

public class CreatePatientProfileCommandFromResourceAssembler {
    public static CreatePatientProfileCommand toCommandFromResource(CreatePatientProfileResource resource) {
        return new CreatePatientProfileCommand(resource.fullName());
    }
}
