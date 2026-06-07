package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record AppointmentResource(
        Long id,
        Long patientId,
        Long doctorProfileId,
        Long familyMemberProfileId,
        String appointmentType,
        String status,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String reason) {
}
