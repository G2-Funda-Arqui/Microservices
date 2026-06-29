package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

import java.time.LocalDateTime;

public record RecordDoseAdministrationCommand(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime administeredAt,
        String notes,
        Long requestedByUserId) {
}
