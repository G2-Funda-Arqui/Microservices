package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Integer> {
    List<Medication> findByPatientId(Long patientId);
    List<Medication> findByPatientIdAndActiveTrue(Long patientId);
}
