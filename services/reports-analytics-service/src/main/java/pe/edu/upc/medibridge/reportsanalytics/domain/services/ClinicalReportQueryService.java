package pe.edu.upc.medibridge.reportsanalytics.domain.services;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.ClinicalReport;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetReportByIdQuery;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetReportsByPatientQuery;

import java.util.List;
import java.util.Optional;

public interface ClinicalReportQueryService {
    Optional<ClinicalReport> handle(GetReportByIdQuery query);
    List<ClinicalReport> handle(GetReportsByPatientQuery query);
}
