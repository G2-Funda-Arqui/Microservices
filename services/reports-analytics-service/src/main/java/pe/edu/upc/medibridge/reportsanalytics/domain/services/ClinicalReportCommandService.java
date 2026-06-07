package pe.edu.upc.medibridge.reportsanalytics.domain.services;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.ClinicalReport;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.commands.GenerateClinicalReportCommand;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.commands.GeneratePdfReportCommand;

import java.util.Optional;

public interface ClinicalReportCommandService {
    Optional<ClinicalReport> handle(GenerateClinicalReportCommand command);
    Optional<ClinicalReport> handle(GeneratePdfReportCommand command);
}
