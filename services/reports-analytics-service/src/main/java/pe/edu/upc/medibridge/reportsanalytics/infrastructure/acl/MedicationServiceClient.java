package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.resources.MedicationSummaryResponse;

@FeignClient(name = "medication-service", url = "${services.medication.url}")
public interface MedicationServiceClient {
    @GetMapping("/api/v1/internal/medications/patients/{patientId}/summary")
    MedicationSummaryResponse getMedicationSummary(@PathVariable Long patientId);
}
