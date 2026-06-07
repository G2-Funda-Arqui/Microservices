package pe.edu.upc.medibridge.appointments.domain.model.events;

import java.time.Instant;
import java.time.LocalDateTime;

public record AppointmentScheduledEvent(
        Long appointmentId,
        Long patientId,
        Long doctorProfileId,
        Long familyMemberProfileId,
        String appointmentType,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Instant occurredAt,
        int version) {

    public AppointmentScheduledEvent(
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
