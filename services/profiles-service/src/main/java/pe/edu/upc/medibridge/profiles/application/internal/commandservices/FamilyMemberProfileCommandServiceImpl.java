package pe.edu.upc.medibridge.profiles.application.internal.commandservices;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl.ExternalIamContextService;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyMemberProfile;
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateFamilyMemberProfileCommand;
import pe.edu.upc.medibridge.profiles.domain.model.events.FamilyMemberProfileCreatedEvent;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.DuplicateProfileException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.InvalidProfileRequestException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.InvalidUserReferenceException;
import pe.edu.upc.medibridge.profiles.domain.services.FamilyMemberProfileCommandService;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.FamilyMemberProfileRepository;

import java.util.Optional;

@Service
public class FamilyMemberProfileCommandServiceImpl implements FamilyMemberProfileCommandService {

    private final FamilyMemberProfileRepository familyMemberProfileRepository;
    private final ExternalIamContextService externalIamContextService;
    private final ApplicationEventPublisher eventPublisher;

    public FamilyMemberProfileCommandServiceImpl(
            FamilyMemberProfileRepository familyMemberProfileRepository,
            ExternalIamContextService externalIamContextService,
            ApplicationEventPublisher eventPublisher) {
        this.familyMemberProfileRepository = familyMemberProfileRepository;
        this.externalIamContextService = externalIamContextService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<FamilyMemberProfile> handle(CreateFamilyMemberProfileCommand command) {
        validateCommand(command);
        if (!externalIamContextService.userExists(command.userId())) {
            throw new InvalidUserReferenceException(command.userId());
        }
        if (familyMemberProfileRepository.existsByUserId(command.userId())) {
            throw new DuplicateProfileException("Family member profile already exists for user: " + command.userId());
        }

        var familyMemberProfile = familyMemberProfileRepository.save(new FamilyMemberProfile(command));
        eventPublisher.publishEvent(new FamilyMemberProfileCreatedEvent(
                familyMemberProfile.getId(),
                familyMemberProfile.getUserId()));
        return Optional.of(familyMemberProfile);
    }

    private void validateCommand(CreateFamilyMemberProfileCommand command) {
        if (command.userId() == null || command.userId() <= 0) {
            throw new InvalidProfileRequestException("User id is required");
        }
        if (command.fullName() == null || command.fullName().isBlank()) {
            throw new InvalidProfileRequestException("Family member full name is required");
        }
    }
}
