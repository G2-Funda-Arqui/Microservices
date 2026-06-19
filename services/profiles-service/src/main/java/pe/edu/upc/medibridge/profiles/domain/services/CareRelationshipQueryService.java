package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.queries.CanDoctorAttendPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.model.queries.CanFamilyMemberAccessPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetCareTeamMembersByPatientIdQuery;
import pe.edu.upc.medibridge.profiles.domain.model.valueobjects.CareTeamMembers;

public interface CareRelationshipQueryService {
    boolean handle(CanDoctorAttendPatientQuery query);
    boolean handle(CanFamilyMemberAccessPatientQuery query);
    CareTeamMembers handle(GetCareTeamMembersByPatientIdQuery query);
}
