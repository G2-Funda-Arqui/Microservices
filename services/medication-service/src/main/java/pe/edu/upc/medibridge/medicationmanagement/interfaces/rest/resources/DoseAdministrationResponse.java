package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DoseAdministrationStatus;

import java.time.LocalDateTime;

public record DoseAdministrationResponse(
        Integer id,
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime occurredAt,
        DoseAdministrationStatus status,
        String notes) {
}
