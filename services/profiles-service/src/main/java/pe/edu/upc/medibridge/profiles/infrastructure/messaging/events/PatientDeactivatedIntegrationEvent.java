package pe.edu.upc.medibridge.profiles.infrastructure.messaging.events;

import java.time.Instant;

public record PatientDeactivatedIntegrationEvent(
        Long patientId,
        Instant occurredAt,
        int version
) {
    public PatientDeactivatedIntegrationEvent(Long patientId) {
        this(patientId, Instant.now(), 1);
    }
}