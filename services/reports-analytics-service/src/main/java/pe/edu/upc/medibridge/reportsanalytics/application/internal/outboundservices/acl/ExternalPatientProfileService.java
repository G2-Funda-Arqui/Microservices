package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import java.util.Optional;

public interface ExternalPatientProfileService {
    boolean patientExists(Long patientId);
    Optional<String> getPatientFullName(Long patientId);
}
