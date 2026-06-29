package pe.edu.upc.medibridge.healthmonitoring.application.internal.outboundservices.acl;

public interface ExternalPatientAccessService {
    boolean canUserAccessPatient(Long userId, Long patientId);
}
