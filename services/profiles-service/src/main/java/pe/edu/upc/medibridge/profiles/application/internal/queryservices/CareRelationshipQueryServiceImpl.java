package pe.edu.upc.medibridge.profiles.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.domain.model.queries.CanDoctorAttendPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.model.queries.CanFamilyMemberAccessPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetCareTeamMembersByPatientIdQuery;
import pe.edu.upc.medibridge.profiles.domain.model.valueobjects.CareTeamMembers;
import pe.edu.upc.medibridge.profiles.domain.services.CareRelationshipQueryService;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.DoctorPatientAssignmentRepository;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.DoctorProfileRepository;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.FamilyMemberProfileRepository;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.FamilyPatientLinkRepository;

import java.util.LinkedHashSet;

@Service
public class CareRelationshipQueryServiceImpl implements CareRelationshipQueryService {

    private final DoctorPatientAssignmentRepository doctorPatientAssignmentRepository;
    private final FamilyPatientLinkRepository familyPatientLinkRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final FamilyMemberProfileRepository familyMemberProfileRepository;

    public CareRelationshipQueryServiceImpl(
            DoctorPatientAssignmentRepository doctorPatientAssignmentRepository,
            FamilyPatientLinkRepository familyPatientLinkRepository,
            DoctorProfileRepository doctorProfileRepository,
            FamilyMemberProfileRepository familyMemberProfileRepository) {
        this.doctorPatientAssignmentRepository = doctorPatientAssignmentRepository;
        this.familyPatientLinkRepository = familyPatientLinkRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.familyMemberProfileRepository = familyMemberProfileRepository;
    }

    @Override
    public boolean handle(CanDoctorAttendPatientQuery query) {
        return doctorPatientAssignmentRepository.existsByDoctorProfileIdAndPatientIdAndActiveTrue(
                query.doctorProfileId(),
                query.patientId());
    }

    @Override
    public boolean handle(CanFamilyMemberAccessPatientQuery query) {
        return familyPatientLinkRepository.existsByFamilyMemberProfileIdAndPatientIdAndActiveTrue(
                query.familyMemberProfileId(),
                query.patientId());
    }

    @Override
    public CareTeamMembers handle(GetCareTeamMembersByPatientIdQuery query) {
        var doctorProfileIds = doctorPatientAssignmentRepository.findAllByPatientIdAndActiveTrue(query.patientId())
                .stream()
                .map(assignment -> assignment.getDoctorProfileId())
                .distinct()
                .toList();

        var familyMemberProfileIds = familyPatientLinkRepository.findAllByPatientIdAndActiveTrue(query.patientId())
                .stream()
                .map(link -> link.getFamilyMemberProfileId())
                .distinct()
                .toList();

        var careTeamUserIds = new LinkedHashSet<Long>();
        doctorProfileRepository.findAllById(doctorProfileIds)
                .forEach(doctorProfile -> careTeamUserIds.add(doctorProfile.getUserId()));
        familyMemberProfileRepository.findAllById(familyMemberProfileIds)
                .forEach(familyMemberProfile -> careTeamUserIds.add(familyMemberProfile.getUserId()));

        return new CareTeamMembers(
                query.patientId(),
                doctorProfileIds,
                familyMemberProfileIds,
                careTeamUserIds.stream().toList());
    }
}
