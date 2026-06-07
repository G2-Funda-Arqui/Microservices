package pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl;

public interface ExternalProfilesContextService {
    boolean patientExists(Long patientId);
    boolean familyMemberCanAccessPatient(Long familyMemberProfileId, Long patientId);
    boolean doctorCanAttendPatient(Long doctorProfileId, Long patientId);
}
