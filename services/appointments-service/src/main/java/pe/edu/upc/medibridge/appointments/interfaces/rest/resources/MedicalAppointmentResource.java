package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record MedicalAppointmentResource(
        Long id,
        Long patientId,
        Long doctorProfileId,
        String appointmentType,
        String status,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String reason) {
}
