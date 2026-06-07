package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ScheduleFamilyVisitResource(
        Long patientId,
        Long familyMemberProfileId,
        LocalDateTime startsAt,
        Integer durationInMinutes,
        String reason) {
}
