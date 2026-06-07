package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationInventory;

import java.util.Optional;

@Repository
public interface MedicationInventoryRepository extends JpaRepository<MedicationInventory, Integer> {
    Optional<MedicationInventory> findByPatientId(Long patientId);
}
