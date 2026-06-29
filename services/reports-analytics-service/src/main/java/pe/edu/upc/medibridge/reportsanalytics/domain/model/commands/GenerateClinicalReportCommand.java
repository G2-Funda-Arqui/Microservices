package pe.edu.upc.medibridge.reportsanalytics.domain.model.commands;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.ReportType;

import java.time.LocalDate;

public record GenerateClinicalReportCommand(
        Long patientId,
        ReportType reportType,
        LocalDate startDate,
        LocalDate endDate,
        Long requestedByUserId) {
}
