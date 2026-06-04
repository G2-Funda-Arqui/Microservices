package pe.edu.upc.medibridge.profiles.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.profiles.domain.model.queries.CanDoctorAttendPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.model.queries.CanFamilyMemberAccessPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.services.CareRelationshipQueryService;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.DoctorPatientAssignmentRepository;
import pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories.FamilyPatientLinkRepository;

@Service
public class CareRelationshipQueryServiceImpl implements CareRelationshipQueryService {

    private final DoctorPatientAssignmentRepository doctorPatientAssignmentRepository;
    private final FamilyPatientLinkRepository familyPatientLinkRepository;

    public CareRelationshipQueryServiceImpl(
            DoctorPatientAssignmentRepository doctorPatientAssignmentRepository,
            FamilyPatientLinkRepository familyPatientLinkRepository) {
        this.doctorPatientAssignmentRepository = doctorPatientAssignmentRepository;
        this.familyPatientLinkRepository = familyPatientLinkRepository;
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
}
