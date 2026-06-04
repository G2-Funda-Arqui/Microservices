package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyPatientLink;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.FamilyPatientLinkResource;

public class FamilyPatientLinkResourceFromEntityAssembler {
    public static FamilyPatientLinkResource toResourceFromEntity(FamilyPatientLink entity) {
        return new FamilyPatientLinkResource(
                entity.getId(),
                entity.getFamilyMemberProfileId(),
                entity.getPatientId(),
                entity.isActive());
    }
}
