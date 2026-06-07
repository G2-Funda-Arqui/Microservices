package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.ReportType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ClinicalReportResponse(
        Integer id,
        Long patientId,
        ReportType reportType,
        LocalDate periodStartDate,
        LocalDate periodEndDate,
        LocalDateTime generatedAt,
        String summary,
        String pdfPath,
        List<ReportSectionResponse> sections) {
}
