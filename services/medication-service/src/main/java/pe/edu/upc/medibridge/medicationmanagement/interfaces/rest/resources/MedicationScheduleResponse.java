package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.FrequencyType;

import java.time.LocalDate;
import java.time.LocalTime;

public record MedicationScheduleResponse(
        Integer id,
        Integer medicationId,
        Long patientId,
        FrequencyType frequencyType,
        Integer timesPerDay,
        LocalTime administrationTime,
        LocalDate startDate,
        LocalDate endDate,
        boolean active) {
}
