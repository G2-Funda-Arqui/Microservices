package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl.ExternalPatientContextService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Service
public class PatientContextFacade implements ExternalPatientContextService {
    private final PatientReferenceRepository patientReferenceRepository;

    public PatientContextFacade(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @Override
    public boolean patientExists(Long patientId) {
        return patientId != null && patientReferenceRepository.existsByPatientIdAndActiveTrue(patientId);
    }
}
