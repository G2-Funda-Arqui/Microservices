package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import java.time.LocalDateTime;

public record RecordDoseAdministrationRequest(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime administeredAt,
        String notes) {
}
