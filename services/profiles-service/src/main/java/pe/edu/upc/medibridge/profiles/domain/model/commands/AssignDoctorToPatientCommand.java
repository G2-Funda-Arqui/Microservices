package pe.edu.upc.medibridge.profiles.domain.model.commands;

public record AssignDoctorToPatientCommand(Long doctorProfileId, Long patientId, Long requestedByUserId) {
}
