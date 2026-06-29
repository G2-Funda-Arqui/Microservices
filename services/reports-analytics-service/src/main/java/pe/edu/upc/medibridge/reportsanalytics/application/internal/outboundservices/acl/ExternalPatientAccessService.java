package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

public interface ExternalPatientAccessService {
    boolean canUserAccessPatient(Long userId, Long patientId);
}
