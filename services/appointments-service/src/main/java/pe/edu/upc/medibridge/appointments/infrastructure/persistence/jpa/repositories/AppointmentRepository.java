package pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            select a
            from Appointment a
            where a.patientId = :patientId
            order by a.timeSlot.startsAt asc
            """)
    List<Appointment> findByPatientIdOrderByStartsAtAsc(@Param("patientId") Long patientId);

    @Query("""
            select count(a) > 0
            from Appointment a
            where a.patientId = :patientId
              and a.status in :activeStatuses
              and a.timeSlot.startsAt < :endsAt
              and a.timeSlot.endsAt > :startsAt
            """)
    boolean existsOverlappingAppointment(
            @Param("patientId") Long patientId,
            @Param("activeStatuses") Collection<AppointmentStatus> activeStatuses,
            @Param("startsAt") LocalDateTime startsAt,
            @Param("endsAt") LocalDateTime endsAt);
}
