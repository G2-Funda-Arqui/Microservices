package pe.edu.upc.medibridge.appointments.domain.model.aggregates;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleFamilyVisitCommand;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleMedicalAppointmentCommand;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.AppointmentStatus;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.AppointmentType;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.TimeSlot;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "appointments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Appointment extends AuditableAbstractAggregateRoot<Appointment> {

    @NotNull
    @Column(nullable = false)
    private Long patientId;

    private Long familyMemberProfileId;

    private Long doctorProfileId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentType appointmentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startsAt", column = @Column(name = "starts_at", nullable = false)),
            @AttributeOverride(name = "endsAt", column = @Column(name = "ends_at", nullable = false))
    })
    private TimeSlot timeSlot;

    @Size(max = 240)
    @Column(length = 240)
    private String reason;

    public Appointment(ScheduleFamilyVisitCommand command, TimeSlot timeSlot) {
        this.patientId = command.patientId();
        this.familyMemberProfileId = command.familyMemberProfileId();
        this.appointmentType = AppointmentType.FAMILY_VISIT;
        this.status = AppointmentStatus.SCHEDULED;
        this.timeSlot = timeSlot;
        this.reason = command.reason();
    }

    public Appointment(ScheduleMedicalAppointmentCommand command, TimeSlot timeSlot) {
        this.patientId = command.patientId();
        this.doctorProfileId = command.doctorProfileId();
        this.appointmentType = AppointmentType.MEDICAL;
        this.status = AppointmentStatus.SCHEDULED;
        this.timeSlot = timeSlot;
        this.reason = command.reason();
    }

    public boolean hasActiveSchedule() {
        return status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.CONFIRMED;
    }

    public boolean overlaps(TimeSlot candidate) {
        return hasActiveSchedule() && timeSlot.overlaps(candidate);
    }
}
