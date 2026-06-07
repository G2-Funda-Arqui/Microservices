package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RecordDoseAdministrationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.SkipDoseCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DoseAdministrationStatus;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class DoseAdministration extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer medicationId;

    @Column(nullable = false)
    private Integer scheduleId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DoseAdministrationStatus status;

    @Column(length = 250)
    private String notes;

    public DoseAdministration(RecordDoseAdministrationCommand command) {
        this.medicationId = command.medicationId();
        this.scheduleId = command.scheduleId();
        this.patientId = command.patientId();
        this.occurredAt = command.administeredAt();
        this.status = DoseAdministrationStatus.ADMINISTERED;
        this.notes = command.notes();
    }

    public DoseAdministration(SkipDoseCommand command) {
        this.medicationId = command.medicationId();
        this.scheduleId = command.scheduleId();
        this.patientId = command.patientId();
        this.occurredAt = command.skippedAt();
        this.status = DoseAdministrationStatus.SKIPPED;
        this.notes = command.reason();
    }
}
