package pe.edu.upc.medibridge.profiles.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorPatientAssignment;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyPatientLink;
import pe.edu.upc.medibridge.profiles.domain.model.commands.AssignDoctorToPatientCommand;
import pe.edu.upc.medibridge.profiles.domain.model.commands.LinkFamilyMemberToPatientCommand;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.DuplicateCareRelationshipException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.InvalidProfileRequestException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.ProfileNotFoundException;
import pe.edu.upc.medibridge.profiles.domain.services.CareRelationshipCommandService;
import pe.edu.upc.medibridge.profiles.infrastructure.messaging.publishers.ProfileIntegrationEventPublisher;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.DoctorPatientAssignmentRepository;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.DoctorProfileRepository;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.FamilyMemberProfileRepository;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.FamilyPatientLinkRepository;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.PatientProfileRepository;

import java.util.Optional;

@Service
public class CareRelationshipCommandServiceImpl implements CareRelationshipCommandService {

    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final FamilyMemberProfileRepository familyMemberProfileRepository;
    private final DoctorPatientAssignmentRepository doctorPatientAssignmentRepository;
    private final FamilyPatientLinkRepository familyPatientLinkRepository;
    private final ProfileIntegrationEventPublisher integrationEventPublisher;

    public CareRelationshipCommandServiceImpl(
            PatientProfileRepository patientProfileRepository,
            DoctorProfileRepository doctorProfileRepository,
            FamilyMemberProfileRepository familyMemberProfileRepository,
            DoctorPatientAssignmentRepository doctorPatientAssignmentRepository,
            FamilyPatientLinkRepository familyPatientLinkRepository,
            ProfileIntegrationEventPublisher integrationEventPublisher) {
        this.patientProfileRepository = patientProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.familyMemberProfileRepository = familyMemberProfileRepository;
        this.doctorPatientAssignmentRepository = doctorPatientAssignmentRepository;
        this.familyPatientLinkRepository = familyPatientLinkRepository;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<DoctorPatientAssignment> handle(AssignDoctorToPatientCommand command) {
        validatePositiveId(command.patientId(), "Patient id is required");
        validatePositiveId(command.doctorProfileId(), "Doctor profile id is required");
        ensurePatientExists(command.patientId());
        ensureDoctorExists(command.doctorProfileId());

        if (doctorPatientAssignmentRepository.existsByDoctorProfileIdAndPatientIdAndActiveTrue(
                command.doctorProfileId(),
                command.patientId())) {
            throw new DuplicateCareRelationshipException("Doctor is already assigned to patient");
        }

        var assignment = doctorPatientAssignmentRepository.save(new DoctorPatientAssignment(command));
        integrationEventPublisher.publishDoctorAssignedToPatient(
                assignment.getId(),
                assignment.getDoctorProfileId(),
                assignment.getPatientId());
        return Optional.of(assignment);
    }

    @Override
    public Optional<FamilyPatientLink> handle(LinkFamilyMemberToPatientCommand command) {
        validatePositiveId(command.patientId(), "Patient id is required");
        validatePositiveId(command.familyMemberProfileId(), "Family member profile id is required");
        ensurePatientExists(command.patientId());
        ensureFamilyMemberExists(command.familyMemberProfileId());

        if (familyPatientLinkRepository.existsByFamilyMemberProfileIdAndPatientIdAndActiveTrue(
                command.familyMemberProfileId(),
                command.patientId())) {
            throw new DuplicateCareRelationshipException("Family member is already linked to patient");
        }

        var link = familyPatientLinkRepository.save(new FamilyPatientLink(command));
        integrationEventPublisher.publishFamilyMemberAssignedToPatient(
                link.getId(),
                link.getFamilyMemberProfileId(),
                link.getPatientId());
        return Optional.of(link);
    }

    private void validatePositiveId(Long id, String message) {
        if (id == null || id <= 0) {
            throw new InvalidProfileRequestException(message);
        }
    }

    private void ensurePatientExists(Long patientId) {
        if (!patientProfileRepository.existsById(patientId)) {
            throw new ProfileNotFoundException("Patient profile", patientId);
        }
    }

    private void ensureDoctorExists(Long doctorProfileId) {
        if (!doctorProfileRepository.existsById(doctorProfileId)) {
            throw new ProfileNotFoundException("Doctor profile", doctorProfileId);
        }
    }

    private void ensureFamilyMemberExists(Long familyMemberProfileId) {
        if (!familyMemberProfileRepository.existsById(familyMemberProfileId)) {
            throw new ProfileNotFoundException("Family member profile", familyMemberProfileId);
        }
    }
}
