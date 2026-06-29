package pe.edu.upc.medibridge.appointments.domain.model.commands;

import java.time.LocalDateTime;

public record ScheduleMedicalAppointmentCommand(
        Long patientId,
        Long doctorProfileId,
        LocalDateTime startsAt,
        Integer durationInMinutes,
        String reason,
        Long requestedByUserId) {
}
