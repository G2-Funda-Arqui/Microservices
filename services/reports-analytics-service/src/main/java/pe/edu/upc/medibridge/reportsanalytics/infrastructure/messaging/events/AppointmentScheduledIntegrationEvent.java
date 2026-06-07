package pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events;

import java.time.Instant;
import java.time.LocalDateTime;

public record AppointmentScheduledIntegrationEvent(Long appointmentId, Long patientId, Long doctorProfileId, Long familyMemberProfileId, String appointmentType, LocalDateTime startsAt, LocalDateTime endsAt, Instant occurredAt, int version) {
}
