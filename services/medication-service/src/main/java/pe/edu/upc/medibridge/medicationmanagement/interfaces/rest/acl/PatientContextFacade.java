package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.acl;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl.ExternalPatientContextService;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.PatientReference;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.acl.ProfilesServiceClient;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Service
public class PatientContextFacade implements ExternalPatientContextService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientContextFacade.class);

    private final PatientReferenceRepository patientReferenceRepository;
    private final ProfilesServiceClient profilesServiceClient;

    public PatientContextFacade(
            PatientReferenceRepository patientReferenceRepository,
            ProfilesServiceClient profilesServiceClient) {
        this.patientReferenceRepository = patientReferenceRepository;
        this.profilesServiceClient = profilesServiceClient;
    }

    @Override
    public boolean patientExists(Long patientId) {
        if (patientId == null) {
            return false;
        }
        if (patientReferenceRepository.existsByPatientIdAndActiveTrue(patientId)) {
            return true;
        }
        return synchronizePatientReferenceFromProfiles(patientId);
    }

    private boolean synchronizePatientReferenceFromProfiles(Long patientId) {
        try {
            if (!profilesServiceClient.patientExists(patientId)) {
                return false;
            }
            synchronizePatientReference(patientId);
            return true;
        } catch (FeignException.NotFound exception) {
            return false;
        } catch (FeignException exception) {
            LOGGER.warn(
                    "Could not verify patient {} against profiles-service. status={}",
                    patientId,
                    exception.status());
            return false;
        }
    }

    private void synchronizePatientReference(Long patientId) {
        try {
            var profile = profilesServiceClient.getPatientProfileById(patientId);
            var fullName = profile == null || profile.fullName() == null || profile.fullName().isBlank()
                    ? "Patient " + patientId
                    : profile.fullName();

            patientReferenceRepository.findByPatientId(patientId)
                    .ifPresentOrElse(
                            reference -> {
                                reference.reactivate(fullName);
                                patientReferenceRepository.save(reference);
                            },
                            () -> patientReferenceRepository.save(new PatientReference(patientId, fullName)));
        } catch (FeignException exception) {
            LOGGER.warn(
                    "Patient {} exists in profiles-service, but medication-service could not refresh its local reference. status={}",
                    patientId,
                    exception.status());
        }
    }
}
