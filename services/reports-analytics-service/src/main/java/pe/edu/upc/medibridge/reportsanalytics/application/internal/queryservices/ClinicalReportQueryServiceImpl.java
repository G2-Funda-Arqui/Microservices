package pe.edu.upc.medibridge.reportsanalytics.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.ClinicalReport;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetReportByIdQuery;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetReportsByPatientQuery;
import pe.edu.upc.medibridge.reportsanalytics.domain.services.ClinicalReportQueryService;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.persistence.jpa.repositories.ClinicalReportRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClinicalReportQueryServiceImpl implements ClinicalReportQueryService {
    private final ClinicalReportRepository clinicalReportRepository;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public ClinicalReportQueryServiceImpl(
            ClinicalReportRepository clinicalReportRepository,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.clinicalReportRepository = clinicalReportRepository;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    @Override
    public Optional<ClinicalReport> handle(GetReportByIdQuery query) {
        var report = clinicalReportRepository.findById(query.reportId());
        report.ifPresent(value -> authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), value.getPatientId()));
        return report;
    }

    @Override
    public List<ClinicalReport> handle(GetReportsByPatientQuery query) {
        authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), query.patientId());
        return clinicalReportRepository.findByPatientIdOrderByGeneratedAtDesc(query.patientId());
    }
}
