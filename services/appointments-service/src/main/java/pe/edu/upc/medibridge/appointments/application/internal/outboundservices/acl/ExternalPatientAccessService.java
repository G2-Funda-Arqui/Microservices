package pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl;

public interface ExternalPatientAccessService {
    boolean canUserAccessPatient(Long userId, Long patientId);
}
