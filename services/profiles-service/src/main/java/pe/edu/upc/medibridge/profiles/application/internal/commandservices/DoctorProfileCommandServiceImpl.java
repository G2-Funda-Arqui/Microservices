package pe.edu.upc.medibridge.profiles.application.internal.commandservices;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl.ExternalIamContextService;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorProfile;
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateDoctorProfileCommand;
import pe.edu.upc.medibridge.profiles.domain.model.events.DoctorProfileCreatedEvent;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.DuplicateProfileException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.InvalidProfileRequestException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.InvalidUserReferenceException;
import pe.edu.upc.medibridge.profiles.domain.services.DoctorProfileCommandService;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.DoctorProfileRepository;

import java.util.Optional;

@Service
public class DoctorProfileCommandServiceImpl implements DoctorProfileCommandService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final ExternalIamContextService externalIamContextService;
    private final ApplicationEventPublisher eventPublisher;

    public DoctorProfileCommandServiceImpl(
            DoctorProfileRepository doctorProfileRepository,
            ExternalIamContextService externalIamContextService,
            ApplicationEventPublisher eventPublisher) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.externalIamContextService = externalIamContextService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<DoctorProfile> handle(CreateDoctorProfileCommand command) {
        validateCommand(command);
        if (!externalIamContextService.userExists(command.userId())) {
            throw new InvalidUserReferenceException(command.userId());
        }
        if (doctorProfileRepository.existsByUserId(command.userId())) {
            throw new DuplicateProfileException("Doctor profile already exists for user: " + command.userId());
        }

        var doctorProfile = doctorProfileRepository.save(new DoctorProfile(command));
        eventPublisher.publishEvent(new DoctorProfileCreatedEvent(doctorProfile.getId(), doctorProfile.getUserId()));
        return Optional.of(doctorProfile);
    }

    private void validateCommand(CreateDoctorProfileCommand command) {
        if (command.userId() == null || command.userId() <= 0) {
            throw new InvalidProfileRequestException("User id is required");
        }
        if (command.fullName() == null || command.fullName().isBlank()) {
            throw new InvalidProfileRequestException("Doctor full name is required");
        }
    }
}
