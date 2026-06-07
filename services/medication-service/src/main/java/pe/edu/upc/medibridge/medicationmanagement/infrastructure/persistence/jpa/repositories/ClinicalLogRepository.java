package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ClinicalLog;

import java.util.List;

@Repository
public interface ClinicalLogRepository extends JpaRepository<ClinicalLog, Integer> {
    List<ClinicalLog> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
