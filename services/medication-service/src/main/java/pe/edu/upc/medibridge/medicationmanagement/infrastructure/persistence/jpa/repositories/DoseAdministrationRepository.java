package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DoseAdministrationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoseAdministrationRepository extends JpaRepository<DoseAdministration, Integer> {
    List<DoseAdministration> findByMedicationIdOrderByOccurredAtDesc(Integer medicationId);
    long countByPatientId(Long patientId);
    long countByPatientIdAndOccurredAtBetween(Long patientId, LocalDateTime start, LocalDateTime end);
    Optional<DoseAdministration> findByScheduleIdAndStatusAndOccurredAtBetween(
            Integer scheduleId,
            DoseAdministrationStatus status,
            LocalDateTime start,
            LocalDateTime end);
}
