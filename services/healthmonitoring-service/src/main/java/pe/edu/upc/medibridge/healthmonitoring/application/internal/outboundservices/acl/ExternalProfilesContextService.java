package pe.edu.upc.medibridge.healthmonitoring.application.internal.outboundservices.acl;

public interface ExternalProfilesContextService {
    boolean patientExists(Long patientId);
    boolean doctorCanAttendPatient(Long doctorProfileId, Long patientId);
}
