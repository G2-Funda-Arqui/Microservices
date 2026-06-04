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
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreateDoctorProfileCommand;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "doctor_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoctorProfile extends AuditableAbstractAggregateRoot<DoctorProfile> {

    @NotNull
    @Column(nullable = false, unique = true)
    private Long userId;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String fullName;

    public DoctorProfile(CreateDoctorProfileCommand command) {
        this.userId = command.userId();
        this.fullName = command.fullName();
    }
}
