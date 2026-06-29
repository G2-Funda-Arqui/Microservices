package pe.edu.upc.medibridge.reportsanalytics.application.internal.queryservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl.ExternalPatientProfileService;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.AnalyticsDashboard;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.entities.MetricSnapshot;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.entities.TrendIndicator;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetAnalyticsDashboardQuery;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.MetricType;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.TrendDirection;
import pe.edu.upc.medibridge.reportsanalytics.domain.services.AnalyticsDashboardQueryService;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.persistence.jpa.repositories.AnalyticsDashboardRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AnalyticsDashboardQueryServiceImpl implements AnalyticsDashboardQueryService {
    private final AnalyticsDashboardRepository analyticsDashboardRepository;
    private final ExternalPatientProfileService externalPatientProfileService;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public AnalyticsDashboardQueryServiceImpl(
            AnalyticsDashboardRepository analyticsDashboardRepository,
            ExternalPatientProfileService externalPatientProfileService,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.analyticsDashboardRepository = analyticsDashboardRepository;
        this.externalPatientProfileService = externalPatientProfileService;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    @Override
    @Transactional
    public AnalyticsDashboard handle(GetAnalyticsDashboardQuery query) {
        if (!externalPatientProfileService.patientExists(query.patientId())) {
            throw new InvalidPatientReferenceException(query.patientId());
        }
        authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), query.patientId());

        return analyticsDashboardRepository.findByPatientId(query.patientId())
                .map(this::initializeDashboardCollections)
                .orElseGet(() -> {
                    var dashboard = new AnalyticsDashboard(query.patientId());
                    dashboard.addMetricSnapshot(new MetricSnapshot(query.patientId(), MetricType.VITAL_SIGN_RECORDS, BigDecimal.ZERO, "records", LocalDateTime.now()));
                    dashboard.addMetricSnapshot(new MetricSnapshot(query.patientId(), MetricType.MEDICATION_ADHERENCE, BigDecimal.ZERO, "percent", LocalDateTime.now()));
                    dashboard.addTrendIndicator(new TrendIndicator(query.patientId(), MetricType.VITAL_SIGN_RECORDS, TrendDirection.STABLE, "Insufficient historical data; default trend is stable."));
                    return analyticsDashboardRepository.save(dashboard);
                });
    }

    private AnalyticsDashboard initializeDashboardCollections(AnalyticsDashboard dashboard) {
        dashboard.getMetricSnapshots().size();
        dashboard.getTrendIndicators().size();
        return dashboard;
    }
}
