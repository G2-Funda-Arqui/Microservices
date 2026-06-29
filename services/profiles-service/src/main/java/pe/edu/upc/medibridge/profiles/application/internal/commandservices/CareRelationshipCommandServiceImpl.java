package pe.edu.upc.medibridge.profiles.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl.ExternalSubscriptionService;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorPatientAssignment;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorProfile;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyMemberProfile;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyPatientLink;
import pe.edu.upc.medibridge.profiles.domain.model.commands.AssignDoctorToPatientCommand;
import pe.edu.upc.medibridge.profiles.domain.model.commands.LinkFamilyMemberToPatientCommand;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.ActiveSubscriptionRequiredException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.DuplicateCareRelationshipException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.InvalidProfileRequestException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.PatientLimitExceededException;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.ProfileOwnershipRequiredException;
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
    private static final int FAMILY_FREE_MAX_PATIENTS = 1;
    private static final String COMMERCIAL_LINE_FAMILY = "FAMILY";
    private static final String COMMERCIAL_LINE_INSTITUTION = "INSTITUTION";

    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final FamilyMemberProfileRepository familyMemberProfileRepository;
    private final DoctorPatientAssignmentRepository doctorPatientAssignmentRepository;
    private final FamilyPatientLinkRepository familyPatientLinkRepository;
    private final ProfileIntegrationEventPublisher integrationEventPublisher;
    private final ExternalSubscriptionService externalSubscriptionService;

    public CareRelationshipCommandServiceImpl(
            PatientProfileRepository patientProfileRepository,
            DoctorProfileRepository doctorProfileRepository,
            FamilyMemberProfileRepository familyMemberProfileRepository,
            DoctorPatientAssignmentRepository doctorPatientAssignmentRepository,
            FamilyPatientLinkRepository familyPatientLinkRepository,
            ProfileIntegrationEventPublisher integrationEventPublisher,
            ExternalSubscriptionService externalSubscriptionService) {
        this.patientProfileRepository = patientProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.familyMemberProfileRepository = familyMemberProfileRepository;
        this.doctorPatientAssignmentRepository = doctorPatientAssignmentRepository;
        this.familyPatientLinkRepository = familyPatientLinkRepository;
        this.integrationEventPublisher = integrationEventPublisher;
        this.externalSubscriptionService = externalSubscriptionService;
    }

    @Override
    public Optional<DoctorPatientAssignment> handle(AssignDoctorToPatientCommand command) {
        validatePositiveId(command.patientId(), "Patient id is required");
        validatePositiveId(command.doctorProfileId(), "Doctor profile id is required");
        validatePositiveId(command.requestedByUserId(), "Authenticated user id is required");
        ensurePatientExists(command.patientId());
        var doctorProfile = getDoctorProfile(command.doctorProfileId());
        ensureProfileBelongsToUser(doctorProfile.getUserId(), command.requestedByUserId(), "Doctor profile does not belong to authenticated user");

        if (doctorPatientAssignmentRepository.existsByDoctorProfileIdAndPatientIdAndActiveTrue(
                command.doctorProfileId(),
                command.patientId())) {
            throw new DuplicateCareRelationshipException("Doctor is already assigned to patient");
        }

        enforceInstitutionSubscriptionLimit(doctorProfile);

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
        validatePositiveId(command.requestedByUserId(), "Authenticated user id is required");
        ensurePatientExists(command.patientId());
        var familyMemberProfile = getFamilyMemberProfile(command.familyMemberProfileId());
        ensureProfileBelongsToUser(familyMemberProfile.getUserId(), command.requestedByUserId(), "Family member profile does not belong to authenticated user");

        if (familyPatientLinkRepository.existsByFamilyMemberProfileIdAndPatientIdAndActiveTrue(
                command.familyMemberProfileId(),
                command.patientId())) {
            throw new DuplicateCareRelationshipException("Family member is already linked to patient");
        }

        enforceFamilySubscriptionLimit(familyMemberProfile);

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

    private DoctorProfile getDoctorProfile(Long doctorProfileId) {
        return doctorProfileRepository.findById(doctorProfileId)
                .orElseThrow(() -> new ProfileNotFoundException("Doctor profile", doctorProfileId));
    }

    private FamilyMemberProfile getFamilyMemberProfile(Long familyMemberProfileId) {
        return familyMemberProfileRepository.findById(familyMemberProfileId)
                .orElseThrow(() -> new ProfileNotFoundException("Family member profile", familyMemberProfileId));
    }

    private void enforceInstitutionSubscriptionLimit(DoctorProfile doctorProfile) {
        var subscription = externalSubscriptionService.findActiveSubscriptionByUserId(doctorProfile.getUserId())
                .filter(activeSubscription -> COMMERCIAL_LINE_INSTITUTION.equals(activeSubscription.commercialLine()))
                .orElseThrow(() -> new ActiveSubscriptionRequiredException(
                        "An active institutional subscription is required to assign patients to a doctor"));

        enforceMaxPatients(
                doctorPatientAssignmentRepository.countActivePatientsByDoctorProfileId(doctorProfile.getId()),
                subscription.maxPatients(),
                "Doctor patient limit reached for current institutional plan");
    }

    private void enforceFamilySubscriptionLimit(FamilyMemberProfile familyMemberProfile) {
        var maxPatients = externalSubscriptionService.findActiveSubscriptionByUserId(familyMemberProfile.getUserId())
                .filter(activeSubscription -> COMMERCIAL_LINE_FAMILY.equals(activeSubscription.commercialLine()))
                .map(ExternalSubscriptionService.ActiveSubscription::maxPatients)
                .orElse(FAMILY_FREE_MAX_PATIENTS);

        enforceMaxPatients(
                familyPatientLinkRepository.countActivePatientsByFamilyMemberProfileId(familyMemberProfile.getId()),
                maxPatients,
                "Family member patient limit reached for current plan");
    }

    private void enforceMaxPatients(long currentActivePatients, Integer maxPatients, String message) {
        if (maxPatients == null || maxPatients <= 0 || currentActivePatients >= maxPatients) {
            throw new PatientLimitExceededException(message);
        }
    }

    private void ensureProfileBelongsToUser(Long profileUserId, Long requestedByUserId, String message) {
        if (!profileUserId.equals(requestedByUserId)) {
            throw new ProfileOwnershipRequiredException(message);
        }
    }
}
