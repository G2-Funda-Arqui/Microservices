package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.ReportType;

import java.time.LocalDate;

public record GenerateReportRequest(
        Long patientId,
        ReportType reportType,
        LocalDate startDate,
        LocalDate endDate) {
}
