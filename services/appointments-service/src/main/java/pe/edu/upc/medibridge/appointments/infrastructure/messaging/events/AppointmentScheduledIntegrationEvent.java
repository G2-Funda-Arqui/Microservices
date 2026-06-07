package pe.edu.upc.medibridge.appointments.infrastructure.messaging.events;

import java.time.Instant;
import java.time.LocalDateTime;

public record AppointmentScheduledIntegrationEvent(
        Long appointmentId,
        Long patientId,
        Long doctorProfileId,
        Long familyMemberProfileId,
        String appointmentType,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Instant occurredAt,
        int version
) {
    public AppointmentScheduledIntegrationEvent(
            Long appointmentId,
            Long patientId,
            Long doctorProfileId,
            Long familyMemberProfileId,
            String appointmentType,
            LocalDateTime startsAt,
            LocalDateTime endsAt) {
        this(appointmentId, patientId, doctorProfileId, familyMemberProfileId, appointmentType, startsAt, endsAt, Instant.now(), 1);
    }
}
