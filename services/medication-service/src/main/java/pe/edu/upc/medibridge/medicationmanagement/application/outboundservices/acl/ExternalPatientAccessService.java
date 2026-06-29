package pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl;

public interface ExternalPatientAccessService {
    boolean canUserAccessPatient(Long userId, Long patientId);
}
