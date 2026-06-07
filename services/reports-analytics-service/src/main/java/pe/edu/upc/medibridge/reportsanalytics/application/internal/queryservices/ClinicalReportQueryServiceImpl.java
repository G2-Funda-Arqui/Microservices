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

    public ClinicalReportQueryServiceImpl(ClinicalReportRepository clinicalReportRepository) {
        this.clinicalReportRepository = clinicalReportRepository;
    }

    @Override
    public Optional<ClinicalReport> handle(GetReportByIdQuery query) {
        return clinicalReportRepository.findById(query.reportId());
    }

    @Override
    public List<ClinicalReport> handle(GetReportsByPatientQuery query) {
        return clinicalReportRepository.findByPatientIdOrderByGeneratedAtDesc(query.patientId());
    }
}
