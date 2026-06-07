package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import java.time.LocalDateTime;

public record SkipDoseRequest(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime skippedAt,
        String reason) {
}
