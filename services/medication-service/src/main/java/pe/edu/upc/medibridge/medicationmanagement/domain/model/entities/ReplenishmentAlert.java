package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

@Getter
@Entity
@NoArgsConstructor
public class ReplenishmentAlert extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer medicationId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private boolean resolved;

    public ReplenishmentAlert(Integer medicationId, Long patientId, Integer currentStock) {
        this.medicationId = medicationId;
        this.patientId = patientId;
        this.currentStock = currentStock;
        this.resolved = false;
    }

    public void resolve() {
        this.resolved = true;
    }
}
