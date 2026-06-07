package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record DoseAdministeredEvent(Integer medicationId, Integer scheduleId, Long patientId, Instant occurredAt, int version) {
    public DoseAdministeredEvent(Integer medicationId, Integer scheduleId, Long patientId) {
        this(medicationId, scheduleId, patientId, Instant.now(), 1);
    }
}
