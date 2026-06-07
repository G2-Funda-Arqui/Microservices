package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "healthmonitoring-service", url = "${services.healthmonitoring.url}")
public interface HealthMonitoringServiceClient {
    @GetMapping("/api/v1/internal/health-monitoring/patients/{patientId}/summary")
    String getPatientHealthSummary(@PathVariable Long patientId);
}
