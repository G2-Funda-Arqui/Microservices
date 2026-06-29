package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.resources.MedicationSummaryResponse;

import java.time.LocalDate;

@FeignClient(name = "medication-service", url = "${services.medication.url}")
public interface MedicationServiceClient {
    @GetMapping("/api/v1/internal/medications/patients/{patientId}/summary")
    MedicationSummaryResponse getMedicationSummary(
            @PathVariable Long patientId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
}
