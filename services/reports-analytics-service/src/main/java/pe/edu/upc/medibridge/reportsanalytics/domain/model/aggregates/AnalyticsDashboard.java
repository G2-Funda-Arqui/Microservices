package pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.entities.MetricSnapshot;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.entities.TrendIndicator;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "analytics_dashboards")
@NoArgsConstructor
public class AnalyticsDashboard extends AuditableAbstractAggregateRoot<AnalyticsDashboard> {
    @Column(nullable = false, unique = true)
    private Long patientId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "analytics_dashboard_id")
    private List<MetricSnapshot> metricSnapshots = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "analytics_dashboard_id")
    private List<TrendIndicator> trendIndicators = new ArrayList<>();

    public AnalyticsDashboard(Long patientId) {
        this.patientId = patientId;
    }

    public void addMetricSnapshot(MetricSnapshot metricSnapshot) {
        this.metricSnapshots.add(metricSnapshot);
    }

    public void addTrendIndicator(TrendIndicator trendIndicator) {
        this.trendIndicators.add(trendIndicator);
    }
}
