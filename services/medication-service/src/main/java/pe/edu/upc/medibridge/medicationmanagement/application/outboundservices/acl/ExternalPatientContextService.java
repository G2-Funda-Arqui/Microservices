package pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl;

public interface ExternalPatientContextService {
    boolean patientExists(Long patientId);
}
