package pe.edu.upc.medibridge.appointments.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl.ExternalProfilesContextService;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.DoctorPatientRelationRepository;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.FamilyPatientRelationRepository;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Service
public class ProfilesContextFacade implements ExternalProfilesContextService {

    private final PatientReferenceRepository patientReferenceRepository;
    private final DoctorPatientRelationRepository doctorPatientRelationRepository;
    private final FamilyPatientRelationRepository familyPatientRelationRepository;

    public ProfilesContextFacade(
            PatientReferenceRepository patientReferenceRepository,
            DoctorPatientRelationRepository doctorPatientRelationRepository,
            FamilyPatientRelationRepository familyPatientRelationRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
        this.doctorPatientRelationRepository = doctorPatientRelationRepository;
        this.familyPatientRelationRepository = familyPatientRelationRepository;
    }

    @Override
    public boolean patientExists(Long patientId) {
        return patientId != null && patientReferenceRepository.existsByPatientIdAndActiveTrue(patientId);
    }

    @Override
    public boolean familyMemberCanAccessPatient(Long familyMemberProfileId, Long patientId) {
        return familyMemberProfileId != null
                && patientId != null
                && familyPatientRelationRepository.existsByFamilyMemberProfileIdAndPatientIdAndActiveTrue(
                familyMemberProfileId,
                patientId);
    }

    @Override
    public boolean doctorCanAttendPatient(Long doctorProfileId, Long patientId) {
        return doctorProfileId != null
                && patientId != null
                && doctorPatientRelationRepository.existsByDoctorProfileIdAndPatientIdAndActiveTrue(
                doctorProfileId,
                patientId);
    }
}
