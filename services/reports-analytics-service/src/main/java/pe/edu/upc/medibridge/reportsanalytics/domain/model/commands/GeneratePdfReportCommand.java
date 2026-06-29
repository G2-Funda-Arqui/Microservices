package pe.edu.upc.medibridge.reportsanalytics.domain.model.commands;

public record GeneratePdfReportCommand(Integer reportId, Long requestedByUserId) {
}
