package pe.edu.upc.medibridge.reportsanalytics.domain.services;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.AnalyticsDashboard;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetAnalyticsDashboardQuery;

public interface AnalyticsDashboardQueryService {
    AnalyticsDashboard handle(GetAnalyticsDashboardQuery query);
}
