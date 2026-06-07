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
@Table(name = "patient_references")
@NoArgsConstructor
public class PatientReference extends AuditableAbstractAggregateRoot<PatientReference> {
    @Column(nullable = false, unique = true)
    private Long patientId;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false)
    private boolean active;

    public PatientReference(Long patientId, String fullName) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
