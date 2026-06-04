package pe.edu.upc.medibridge.profiles.interfaces.rest.resources;

public record DoctorPatientAssignmentResource(
        Long id,
        Long doctorProfileId,
        Long patientId,
        boolean active) {
}
