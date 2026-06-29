package pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorPatientAssignment;

import java.util.List;

@Repository
public interface DoctorPatientAssignmentRepository extends JpaRepository<DoctorPatientAssignment, Long> {
    boolean existsByDoctorProfileIdAndPatientIdAndActiveTrue(Long doctorProfileId, Long patientId);
    List<DoctorPatientAssignment> findAllByPatientIdAndActiveTrue(Long patientId);

    @Query("select count(distinct assignment.patientId) from DoctorPatientAssignment assignment where assignment.doctorProfileId = :doctorProfileId and assignment.active = true")
    long countActivePatientsByDoctorProfileId(@Param("doctorProfileId") Long doctorProfileId);
}
