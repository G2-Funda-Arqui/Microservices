package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.transform;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.AnalyticsDashboard;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources.DashboardMetricsResponse;

public class DashboardMetricsResponseFromEntityAssembler {
    public static DashboardMetricsResponse toResourceFromEntity(AnalyticsDashboard entity) {
        var metrics = entity.getMetricSnapshots().stream()
                .map(metric -> new DashboardMetricsResponse.MetricSnapshotResponse(
                        metric.getId(),
                        metric.getMetricType(),
                        metric.getValue(),
                        metric.getUnit(),
                        metric.getCapturedAt()))
                .toList();
        var trends = entity.getTrendIndicators().stream()
                .map(trend -> new DashboardMetricsResponse.TrendIndicatorResponse(
                        trend.getId(),
                        trend.getMetricType(),
                        trend.getDirection(),
                        trend.getExplanation()))
                .toList();
        return new DashboardMetricsResponse(entity.getId(), entity.getPatientId(), metrics, trends);
    }
}
