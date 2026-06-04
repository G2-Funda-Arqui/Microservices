package pe.edu.upc.medibridge.profiles.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateFamilyMemberProfileCommand;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "family_member_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyMemberProfile extends AuditableAbstractAggregateRoot<FamilyMemberProfile> {

    @NotNull
    @Column(nullable = false, unique = true)
    private Long userId;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String fullName;

    public FamilyMemberProfile(CreateFamilyMemberProfileCommand command) {
        this.userId = command.userId();
        this.fullName = command.fullName();
    }
}
