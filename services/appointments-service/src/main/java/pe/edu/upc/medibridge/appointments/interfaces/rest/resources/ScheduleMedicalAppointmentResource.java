package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ScheduleMedicalAppointmentResource(
        Long patientId,
        Long doctorProfileId,
        LocalDateTime startsAt,
        Integer durationInMinutes,
        String reason) {
}
