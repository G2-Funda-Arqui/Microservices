package pe.edu.upc.medibridge.appointments.infrastructure.messaging.events;

import java.time.Instant;

public record PatientDeactivatedIntegrationEvent(
        Long patientId,
        Instant occurredAt,
        int version
) {
}
