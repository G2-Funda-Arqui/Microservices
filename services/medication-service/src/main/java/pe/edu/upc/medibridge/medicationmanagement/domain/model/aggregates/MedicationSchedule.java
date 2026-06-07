package pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.CreateMedicationScheduleCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.FrequencyType;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor
public class MedicationSchedule extends AuditableAbstractAggregateRoot<MedicationSchedule> {
    @Column(nullable = false)
    private Integer medicationId;

    @Column(nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FrequencyType frequencyType;

    @Column(nullable = false)
    private Integer timesPerDay;

    @Column(nullable = false)
    private LocalTime administrationTime;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active;

    public MedicationSchedule(CreateMedicationScheduleCommand command) {
        this.medicationId = command.medicationId();
        this.patientId = command.patientId();
        this.frequencyType = command.frequencyType();
        this.timesPerDay = command.timesPerDay();
        this.administrationTime = command.administrationTime();
        this.startDate = command.startDate();
        this.endDate = command.endDate();
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
