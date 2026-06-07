package pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.entities.PatientReference;

import java.util.Optional;

@Repository
public interface PatientReferenceRepository extends JpaRepository<PatientReference, Long> {
    boolean existsByPatientIdAndActiveTrue(Long patientId);
    Optional<PatientReference> findByPatientId(Long patientId);
}
