package pe.edu.upc.medibridge.reportsanalytics.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.MetricType;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.TrendDirection;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

@Getter
@Entity
@Table(name = "trend_indicators")
@NoArgsConstructor
public class TrendIndicator extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private MetricType metricType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TrendDirection direction;

    @Column(nullable = false, length = 300)
    private String explanation;

    public TrendIndicator(Long patientId, MetricType metricType, TrendDirection direction, String explanation) {
        this.patientId = patientId;
        this.metricType = metricType;
        this.direction = direction;
        this.explanation = explanation;
    }
}
