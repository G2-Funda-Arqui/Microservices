package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.valueobjects.CareTeamMembers;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.CareTeamMembersResource;

public class CareTeamMembersResourceFromValueObjectAssembler {
    public static CareTeamMembersResource toResourceFromValueObject(CareTeamMembers members) {
        return new CareTeamMembersResource(
                members.patientId(),
                members.doctorProfileIds(),
                members.familyMemberProfileIds(),
                members.careTeamUserIds());
    }
}
