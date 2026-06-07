package pe.edu.upc.medibridge.appointments.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "family_patient_relations")
@NoArgsConstructor
public class FamilyPatientRelation extends AuditableAbstractAggregateRoot<FamilyPatientRelation> {
    @Column(nullable = false)
    private Long linkId;

    @Column(nullable = false)
    private Long familyMemberProfileId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private boolean active;

    public FamilyPatientRelation(Long linkId, Long familyMemberProfileId, Long patientId) {
        this.linkId = linkId;
        this.familyMemberProfileId = familyMemberProfileId;
        this.patientId = patientId;
        this.active = true;
    }
}
