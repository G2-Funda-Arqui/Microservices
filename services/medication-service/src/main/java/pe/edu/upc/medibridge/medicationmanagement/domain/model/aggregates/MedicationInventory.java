package pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class MedicationInventory extends AuditableAbstractAggregateRoot<MedicationInventory> {
    @Column(nullable = false, unique = true)
    private Long patientId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inventory_id")
    private List<Medication> medications = new ArrayList<>();

    public MedicationInventory(Long patientId) {
        this.patientId = patientId;
    }

    public void addMedication(Medication medication) {
        this.medications.add(medication);
    }
}
