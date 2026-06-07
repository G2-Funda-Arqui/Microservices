package pe.edu.upc.medibridge.reportsanalytics.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.AnalyticsDashboard;

import java.util.Optional;

@Repository
public interface AnalyticsDashboardRepository extends JpaRepository<AnalyticsDashboard, Integer> {
    Optional<AnalyticsDashboard> findByPatientId(Long patientId);
}
