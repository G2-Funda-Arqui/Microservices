package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorPatientAssignment;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyPatientLink;
import pe.edu.upc.medibridge.profiles.domain.model.commands.AssignDoctorToPatientCommand;
import pe.edu.upc.medibridge.profiles.domain.model.commands.LinkFamilyMemberToPatientCommand;

import java.util.Optional;

public interface CareRelationshipCommandService {
    Optional<DoctorPatientAssignment> handle(AssignDoctorToPatientCommand command);
    Optional<FamilyPatientLink> handle(LinkFamilyMemberToPatientCommand command);
}
