package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorProfile;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.DoctorProfileResource;

public class DoctorProfileResourceFromEntityAssembler {
    public static DoctorProfileResource toResourceFromEntity(DoctorProfile entity) {
        return new DoctorProfileResource(entity.getId(), entity.getUserId(), entity.getFullName());
    }
}
