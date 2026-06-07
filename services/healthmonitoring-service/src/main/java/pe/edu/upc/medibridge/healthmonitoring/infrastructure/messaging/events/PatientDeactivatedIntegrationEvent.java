package pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.events;

import java.time.Instant;

public record PatientDeactivatedIntegrationEvent(
        Long patientId,
        Instant occurredAt,
        int version
) {
}
