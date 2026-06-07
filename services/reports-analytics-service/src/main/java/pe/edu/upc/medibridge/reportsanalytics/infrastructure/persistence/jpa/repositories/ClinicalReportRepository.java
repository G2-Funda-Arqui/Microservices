package pe.edu.upc.medibridge.reportsanalytics.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.ClinicalReport;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicalReportRepository extends JpaRepository<ClinicalReport, Integer> {
    @Override
    @EntityGraph(attributePaths = "sections")
    Optional<ClinicalReport> findById(Integer id);

    @EntityGraph(attributePaths = "sections")
    List<ClinicalReport> findByPatientIdOrderByGeneratedAtDesc(Long patientId);
}
