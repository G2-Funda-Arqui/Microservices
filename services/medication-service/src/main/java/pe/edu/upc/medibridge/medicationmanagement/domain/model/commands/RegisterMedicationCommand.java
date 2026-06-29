package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.AdministrationRoute;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DosageUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterMedicationCommand(
        Long patientId,
        String name,
        BigDecimal dosageAmount,
        DosageUnit dosageUnit,
        AdministrationRoute administrationRoute,
        Integer stockQuantity,
        Integer lowStockThreshold,
        LocalDate expirationDate,
        Long requestedByUserId) {
}
