package pe.edu.upc.medibridge.profiles.domain.model.valueobjects;

import java.util.List;

public record CareTeamMembers(
        Long patientId,
        List<Long> doctorProfileIds,
        List<Long> familyMemberProfileIds,
        List<Long> careTeamUserIds) {
}
