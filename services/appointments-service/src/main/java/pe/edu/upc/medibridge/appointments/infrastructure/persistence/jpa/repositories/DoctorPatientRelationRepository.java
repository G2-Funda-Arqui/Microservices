package pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.appointments.domain.model.entities.DoctorPatientRelation;

@Repository
public interface DoctorPatientRelationRepository extends JpaRepository<DoctorPatientRelation, Long> {
    boolean existsByAssignmentId(Long assignmentId);
    boolean existsByDoctorProfileIdAndPatientIdAndActiveTrue(Long doctorProfileId, Long patientId);
}
