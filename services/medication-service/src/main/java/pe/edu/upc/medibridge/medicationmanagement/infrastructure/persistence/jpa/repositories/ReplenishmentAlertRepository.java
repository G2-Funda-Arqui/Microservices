package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ReplenishmentAlert;

import java.util.List;

@Repository
public interface ReplenishmentAlertRepository extends JpaRepository<ReplenishmentAlert, Integer> {
    List<ReplenishmentAlert> findByPatientIdAndResolvedFalse(Long patientId);
}
