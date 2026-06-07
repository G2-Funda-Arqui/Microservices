package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record StockLowIntegrationEvent(
        Integer medicationId,
        Long patientId,
        String medicationName,
        Integer currentStock,
        Integer threshold,
        Instant occurredAt,
        int version) {
}
