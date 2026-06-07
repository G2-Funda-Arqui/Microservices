package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "patient_references")
public class PatientReference extends AuditableAbstractAggregateRoot<PatientReference> {

    @Column(nullable = false, unique = true)
    private Long patientId;

    @Column(nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false)
    private boolean active;

    public PatientReference(Long patientId, String fullName) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.active = true;
    }

    public void reactivate(String fullName) {
        this.fullName = fullName;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
