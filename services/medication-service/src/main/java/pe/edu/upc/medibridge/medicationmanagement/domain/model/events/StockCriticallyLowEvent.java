package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record StockCriticallyLowEvent(Integer medicationId, Long patientId, Integer currentStock, Instant occurredAt, int version) {
    public StockCriticallyLowEvent(Integer medicationId, Long patientId, Integer currentStock) {
        this(medicationId, patientId, currentStock, Instant.now(), 1);
    }
}
