package pe.edu.upc.medibridge.appointments.domain.model.commands;

import java.time.LocalDateTime;

public record ScheduleFamilyVisitCommand(
        Long patientId,
        Long familyMemberProfileId,
        LocalDateTime startsAt,
        Integer durationInMinutes,
        String reason) {
}
