package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record MedicationExpiredEvent(Integer medicationId, Long patientId, Instant occurredAt, int version) {
    public MedicationExpiredEvent(Integer medicationId, Long patientId) {
        this(medicationId, patientId, Instant.now(), 1);
    }
}
