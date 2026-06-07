package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record PatientRegisteredIntegrationEvent(
        Long patientId,
        String fullName,
        Instant occurredAt,
        int version) {
}
