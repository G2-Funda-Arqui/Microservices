package pe.edu.upc.medibridge.appointments.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "doctor_patient_relations")
@NoArgsConstructor
public class DoctorPatientRelation extends AuditableAbstractAggregateRoot<DoctorPatientRelation> {
    @Column(nullable = false)
    private Long assignmentId;

    @Column(nullable = false)
    private Long doctorProfileId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private boolean active;

    public DoctorPatientRelation(Long assignmentId, Long doctorProfileId, Long patientId) {
        this.assignmentId = assignmentId;
        this.doctorProfileId = doctorProfileId;
        this.patientId = patientId;
        this.active = true;
    }
}
