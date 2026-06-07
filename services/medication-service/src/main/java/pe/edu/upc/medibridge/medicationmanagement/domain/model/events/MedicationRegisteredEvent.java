package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record MedicationRegisteredEvent(Integer medicationId, Long patientId, Instant occurredAt, int version) {
    public MedicationRegisteredEvent(Integer medicationId, Long patientId) {
        this(medicationId, patientId, Instant.now(), 1);
    }
}
