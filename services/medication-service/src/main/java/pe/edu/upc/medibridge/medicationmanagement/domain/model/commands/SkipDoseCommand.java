package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

import java.time.LocalDateTime;

public record SkipDoseCommand(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime skippedAt,
        String reason) {
}
