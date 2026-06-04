package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyMemberProfile;
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateFamilyMemberProfileCommand;

import java.util.Optional;

public interface FamilyMemberProfileCommandService {
    Optional<FamilyMemberProfile> handle(CreateFamilyMemberProfileCommand command);
}
