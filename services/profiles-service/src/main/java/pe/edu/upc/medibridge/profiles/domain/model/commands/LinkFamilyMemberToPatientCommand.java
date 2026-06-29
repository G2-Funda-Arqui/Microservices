package pe.edu.upc.medibridge.profiles.domain.model.commands;

public record LinkFamilyMemberToPatientCommand(Long familyMemberProfileId, Long patientId, Long requestedByUserId) {
}
