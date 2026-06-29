package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.FrequencyType;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateMedicationScheduleCommand(
        Integer medicationId,
        Long patientId,
        FrequencyType frequencyType,
        Integer timesPerDay,
        LocalTime administrationTime,
        LocalDate startDate,
        LocalDate endDate,
        Long requestedByUserId) {
}
