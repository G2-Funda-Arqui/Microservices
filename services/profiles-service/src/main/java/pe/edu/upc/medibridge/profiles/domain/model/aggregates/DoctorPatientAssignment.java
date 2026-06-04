package pe.edu.upc.medibridge.profiles.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.profiles.domain.model.commands.AssignDoctorToPatientCommand;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "doctor_patient_assignments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoctorPatientAssignment extends AuditableAbstractAggregateRoot<DoctorPatientAssignment> {

    @NotNull
    @Column(nullable = false)
    private Long doctorProfileId;

    @NotNull
    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private boolean active;

    public DoctorPatientAssignment(AssignDoctorToPatientCommand command) {
        this.doctorProfileId = command.doctorProfileId();
        this.patientId = command.patientId();
        this.active = true;
    }
}
