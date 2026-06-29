package pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.PatientHealthObservation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatientHealthObservationRepository extends JpaRepository<PatientHealthObservation, Long> {
    List<PatientHealthObservation> findByPatientIdOrderByRecordedAtDesc(Long patientId);
    List<PatientHealthObservation> findTop5ByPatientIdOrderByRecordedAtDesc(Long patientId);
    List<PatientHealthObservation> findByPatientIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            Long patientId,
            LocalDateTime start,
            LocalDateTime end);
}
