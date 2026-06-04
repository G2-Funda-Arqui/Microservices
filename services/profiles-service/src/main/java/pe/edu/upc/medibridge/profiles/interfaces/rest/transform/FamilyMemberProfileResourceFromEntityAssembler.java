package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyMemberProfile;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.FamilyMemberProfileResource;

public class FamilyMemberProfileResourceFromEntityAssembler {
    public static FamilyMemberProfileResource toResourceFromEntity(FamilyMemberProfile entity) {
        return new FamilyMemberProfileResource(entity.getId(), entity.getUserId(), entity.getFullName());
    }
}
