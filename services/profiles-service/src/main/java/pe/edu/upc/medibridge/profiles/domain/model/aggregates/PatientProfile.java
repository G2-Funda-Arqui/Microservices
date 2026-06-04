package pe.edu.upc.medibridge.profiles.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.profiles.domain.model.commands.CreatePatientProfileCommand;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "patient_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientProfile extends AuditableAbstractAggregateRoot<PatientProfile> {

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String fullName;

    public PatientProfile(CreatePatientProfileCommand command) {
        this.fullName = command.fullName();
    }
}
