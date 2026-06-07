package pe.edu.upc.medibridge.appointments.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeSlot {

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    public TimeSlot(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (startsAt == null || endsAt == null) {
            throw new IllegalArgumentException("Time slot start and end are required");
        }
        if (!startsAt.isBefore(endsAt)) {
            throw new IllegalArgumentException("Time slot start must be before end");
        }
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public boolean overlaps(TimeSlot other) {
        return startsAt.isBefore(other.endsAt) && endsAt.isAfter(other.startsAt);
    }

    public long durationInMinutes() {
        return Duration.between(startsAt, endsAt).toMinutes();
    }
}
