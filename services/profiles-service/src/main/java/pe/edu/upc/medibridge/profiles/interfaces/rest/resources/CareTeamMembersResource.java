package pe.edu.upc.medibridge.profiles.interfaces.rest.resources;

import java.util.List;

public record CareTeamMembersResource(
        Long patientId,
        List<Long> doctorProfileIds,
        List<Long> familyMemberProfileIds,
        List<Long> careTeamUserIds) {
}
