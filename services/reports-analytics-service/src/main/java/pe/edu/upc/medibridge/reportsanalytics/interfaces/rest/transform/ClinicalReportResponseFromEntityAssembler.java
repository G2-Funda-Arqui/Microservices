package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.transform;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.ClinicalReport;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources.ClinicalReportResponse;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources.ReportSectionResponse;

public class ClinicalReportResponseFromEntityAssembler {
    public static ClinicalReportResponse toResourceFromEntity(ClinicalReport entity) {
        var sections = entity.getSections().stream()
                .map(section -> new ReportSectionResponse(
                        section.getId(),
                        section.getTitle(),
                        section.getContent(),
                        section.getDisplayOrder()))
                .toList();
        return new ClinicalReportResponse(
                entity.getId(),
                entity.getPatientId(),
                entity.getReportType(),
                entity.getPeriodStartDate(),
                entity.getPeriodEndDate(),
                entity.getGeneratedAt(),
                entity.getSummary(),
                entity.getPdfPath(),
                sections);
    }
}
