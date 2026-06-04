package pe.edu.upc.medibridge.profiles.domain.model.commands;

public record CreateDoctorProfileCommand(Long userId, String fullName) {
}
