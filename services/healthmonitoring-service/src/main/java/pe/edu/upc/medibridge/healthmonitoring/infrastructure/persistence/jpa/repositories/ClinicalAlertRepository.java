package pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.ClinicalAlert;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.AlertStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClinicalAlertRepository extends JpaRepository<ClinicalAlert, Long> {
    List<ClinicalAlert> findByPatientIdAndStatusOrderByTriggeredAtDesc(Long patientId, AlertStatus status);
    List<ClinicalAlert> findByPatientIdAndStatusAndTriggeredAtBetweenOrderByTriggeredAtDesc(
            Long patientId,
            AlertStatus status,
            LocalDateTime start,
            LocalDateTime end);
}
