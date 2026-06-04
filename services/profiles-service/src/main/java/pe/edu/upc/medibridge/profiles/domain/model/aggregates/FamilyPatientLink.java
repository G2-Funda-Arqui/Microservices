package pe.edu.upc.medibridge.profiles.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.profiles.domain.model.commands.LinkFamilyMemberToPatientCommand;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "family_patient_links")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyPatientLink extends AuditableAbstractAggregateRoot<FamilyPatientLink> {

    @NotNull
    @Column(nullable = false)
    private Long familyMemberProfileId;

    @NotNull
    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private boolean active;

    public FamilyPatientLink(LinkFamilyMemberToPatientCommand command) {
        this.familyMemberProfileId = command.familyMemberProfileId();
        this.patientId = command.patientId();
        this.active = true;
    }
}
