package pe.edu.upc.medibridge.reportsanalytics.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.MetricType;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "metric_snapshots")
@NoArgsConstructor
public class MetricSnapshot extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private MetricType metricType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(nullable = false, length = 30)
    private String unit;

    @Column(nullable = false)
    private LocalDateTime capturedAt;

    public MetricSnapshot(Long patientId, MetricType metricType, BigDecimal value, String unit, LocalDateTime capturedAt) {
        this.patientId = patientId;
        this.metricType = metricType;
        this.value = value;
        this.unit = unit;
        this.capturedAt = capturedAt;
    }
}
