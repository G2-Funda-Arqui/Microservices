package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.healthmonitoring.application.internal.outboundservices.acl.ExternalProfilesContextService;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.DoctorPatientRelationRepository;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Service("healthMonitoringProfilesContextFacade")
public class ProfilesContextFacade implements ExternalProfilesContextService {

    private final PatientReferenceRepository patientReferenceRepository;
    private final DoctorPatientRelationRepository doctorPatientRelationRepository;

    public ProfilesContextFacade(
            PatientReferenceRepository patientReferenceRepository,
            DoctorPatientRelationRepository doctorPatientRelationRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
        this.doctorPatientRelationRepository = doctorPatientRelationRepository;
    }

    @Override
    public boolean patientExists(Long patientId) {
        return patientId != null && patientReferenceRepository.existsByPatientIdAndActiveTrue(patientId);
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
