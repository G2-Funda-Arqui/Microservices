package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.queries.CanDoctorAttendPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.model.queries.CanFamilyMemberAccessPatientQuery;

public interface CareRelationshipQueryService {
    boolean handle(CanDoctorAttendPatientQuery query);
    boolean handle(CanFamilyMemberAccessPatientQuery query);
}
