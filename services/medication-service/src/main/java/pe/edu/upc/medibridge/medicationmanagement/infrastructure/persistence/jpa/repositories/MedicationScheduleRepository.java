package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;

import java.util.List;

@Repository
public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Integer> {
    List<MedicationSchedule> findByPatientIdAndActiveTrue(Long patientId);
    List<MedicationSchedule> findByMedicationIdAndActiveTrue(Integer medicationId);
}
