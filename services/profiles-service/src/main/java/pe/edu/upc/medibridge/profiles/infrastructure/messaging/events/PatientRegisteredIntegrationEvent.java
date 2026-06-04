package pe.edu.upc.medibridge.profiles.infrastructure.messaging.events;

import java.time.Instant;

public record PatientRegisteredIntegrationEvent(
        Long patientId,
        String fullName,
        Instant occurredAt,
        int version
) {
    public PatientRegisteredIntegrationEvent(Long patientId, String fullName) {
        this(patientId, fullName, Instant.now(), 1);
    }
}