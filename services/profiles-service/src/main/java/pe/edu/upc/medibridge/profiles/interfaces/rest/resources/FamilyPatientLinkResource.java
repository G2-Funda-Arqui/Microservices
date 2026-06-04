package pe.edu.upc.medibridge.profiles.interfaces.rest.resources;

public record FamilyPatientLinkResource(
        Long id,
        Long familyMemberProfileId,
        Long patientId,
        boolean active) {
}
