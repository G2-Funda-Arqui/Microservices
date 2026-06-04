package pe.edu.upc.medibridge.profiles.domain.services;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyMemberProfile;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetFamilyMemberProfileByIdQuery;

import java.util.Optional;

public interface FamilyMemberProfileQueryService {
    Optional<FamilyMemberProfile> handle(GetFamilyMemberProfileByIdQuery query);
}
