package pe.edu.upc.medibridge.profiles.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.PatientProfile;
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreatePatientProfileCommand;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.InvalidProfileRequestException;
import pe.edu.upc.medibridge.profiles.domain.services.PatientProfileCommandService;
import pe.edu.upc.medibridge.profiles.infrastructure.messaging.publishers.ProfileIntegrationEventPublisher;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.PatientProfileRepository;

import java.util.Optional;

@Service
public class PatientProfileCommandServiceImpl implements PatientProfileCommandService {

    private final PatientProfileRepository patientProfileRepository;
    private final ProfileIntegrationEventPublisher integrationEventPublisher;

    public PatientProfileCommandServiceImpl(
            PatientProfileRepository patientProfileRepository,
            ProfileIntegrationEventPublisher integrationEventPublisher) {
        this.patientProfileRepository = patientProfileRepository;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<PatientProfile> handle(CreatePatientProfileCommand command) {
        validateFullName(command.fullName());
        var patientProfile = patientProfileRepository.save(new PatientProfile(command));
        integrationEventPublisher.publishPatientRegistered(patientProfile.getId(), patientProfile.getFullName());
        return Optional.of(patientProfile);
    }

    private void validateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new InvalidProfileRequestException("Patient full name is required");
        }
    }
}
