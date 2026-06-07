package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.MetricType;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.TrendDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DashboardMetricsResponse(
        Integer id,
        Long patientId,
        List<MetricSnapshotResponse> metricSnapshots,
        List<TrendIndicatorResponse> trendIndicators) {

    public record MetricSnapshotResponse(
            Integer id,
            MetricType metricType,
            BigDecimal value,
            String unit,
            LocalDateTime capturedAt) {
    }

    public record TrendIndicatorResponse(
            Integer id,
            MetricType metricType,
            TrendDirection direction,
            String explanation) {
    }
}
