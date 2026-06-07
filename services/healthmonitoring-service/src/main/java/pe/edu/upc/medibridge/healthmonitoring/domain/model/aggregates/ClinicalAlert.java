package pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.AlertSeverity;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.AlertStatus;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "clinical_alerts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClinicalAlert extends AuditableAbstractAggregateRoot<ClinicalAlert> {

    @NotNull
    @Column(nullable = false)
    private Long patientId;

    @NotNull
    @Column(nullable = false)
    private Long observationId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertSeverity severity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status;

    @NotNull
    @Column(nullable = false, length = 500)
    private String message;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime triggeredAt;

    public ClinicalAlert(
            Long patientId,
            Long observationId,
            AlertSeverity severity,
            String message,
            LocalDateTime triggeredAt) {
        this.patientId = patientId;
        this.observationId = observationId;
        this.severity = severity;
        this.status = AlertStatus.ACTIVE;
        this.message = message;
        this.triggeredAt = triggeredAt;
    }
}
