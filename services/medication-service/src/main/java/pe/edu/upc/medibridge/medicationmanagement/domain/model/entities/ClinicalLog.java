package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

@Getter
@Entity
@NoArgsConstructor
public class ClinicalLog extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long patientId;

    private Integer medicationId;

    @Column(nullable = false, length = 500)
    private String description;

    public ClinicalLog(Long patientId, Integer medicationId, String description) {
        this.patientId = patientId;
        this.medicationId = medicationId;
        this.description = description;
    }
}
