package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "healthmonitoring-service", url = "${services.healthmonitoring.url}")
public interface HealthMonitoringServiceClient {
    @GetMapping("/api/v1/internal/health-monitoring/patients/{patientId}/summary")
    String getPatientHealthSummary(
            @PathVariable Long patientId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
}
