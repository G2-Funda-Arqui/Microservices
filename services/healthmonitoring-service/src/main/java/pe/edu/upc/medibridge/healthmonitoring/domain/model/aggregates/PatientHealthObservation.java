package pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.commands.RecordPatientHealthObservationCommand;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.EmotionalState;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "patient_health_observations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientHealthObservation extends AuditableAbstractAggregateRoot<PatientHealthObservation> {

    @NotNull
    @Column(nullable = false)
    private Long patientId;

    @NotNull
    @Column(nullable = false)
    private Long recordedByDoctorProfileId;

    @NotNull
    @Column(nullable = false)
    private Integer systolicBloodPressure;

    @NotNull
    @Column(nullable = false)
    private Integer diastolicBloodPressure;

    @NotNull
    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal bodyTemperature;

    @NotNull
    @Column(nullable = false)
    private Integer painLevel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmotionalState emotionalState;

    @Size(max = 240)
    @Column(length = 240)
    private String emotionalNotes;

    @Size(max = 500)
    @Column(length = 500)
    private String clinicalNotes;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime recordedAt;

    public PatientHealthObservation(RecordPatientHealthObservationCommand command) {
        this.patientId = command.patientId();
        this.recordedByDoctorProfileId = command.recordedByDoctorProfileId();
        this.systolicBloodPressure = command.systolicBloodPressure();
        this.diastolicBloodPressure = command.diastolicBloodPressure();
        this.bodyTemperature = command.bodyTemperature();
        this.painLevel = command.painLevel();
        this.emotionalState = command.emotionalState();
        this.emotionalNotes = command.emotionalNotes();
        this.clinicalNotes = command.clinicalNotes();
        this.recordedAt = command.recordedAt();
    }
}
