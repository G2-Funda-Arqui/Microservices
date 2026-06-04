package pe.edu.upc.medibridge.profiles.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyMemberProfile;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetFamilyMemberProfileByIdQuery;
import pe.edu.upc.medibridge.profiles.domain.services.FamilyMemberProfileQueryService;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.FamilyMemberProfileRepository;

import java.util.Optional;

@Service
public class FamilyMemberProfileQueryServiceImpl implements FamilyMemberProfileQueryService {

    private final FamilyMemberProfileRepository familyMemberProfileRepository;

    public FamilyMemberProfileQueryServiceImpl(FamilyMemberProfileRepository familyMemberProfileRepository) {
        this.familyMemberProfileRepository = familyMemberProfileRepository;
    }

    @Override
    public Optional<FamilyMemberProfile> handle(GetFamilyMemberProfileByIdQuery query) {
        return familyMemberProfileRepository.findById(query.familyMemberProfileId());
    }
}
