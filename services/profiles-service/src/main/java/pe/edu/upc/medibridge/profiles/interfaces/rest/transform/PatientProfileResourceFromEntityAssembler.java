package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.PatientProfile;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.PatientProfileResource;

public class PatientProfileResourceFromEntityAssembler {
    public static PatientProfileResource toResourceFromEntity(PatientProfile entity) {
        return new PatientProfileResource(entity.getId(), entity.getFullName());
    }
}
