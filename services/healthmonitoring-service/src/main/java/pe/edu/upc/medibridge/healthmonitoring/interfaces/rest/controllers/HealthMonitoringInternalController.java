package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.acl.HealthMonitoringContextFacade;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/internal/health-monitoring")
public class HealthMonitoringInternalController {
    private final HealthMonitoringContextFacade healthMonitoringContextFacade;

    public HealthMonitoringInternalController(HealthMonitoringContextFacade healthMonitoringContextFacade) {
        this.healthMonitoringContextFacade = healthMonitoringContextFacade;
    }

    @GetMapping("/patients/{patientId}/summary")
    public String getPatientHealthSummary(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return healthMonitoringContextFacade.fetchPatientClinicalSummaryByPatientIdAndPeriod(patientId, startDate, endDate);
        }
        return healthMonitoringContextFacade.fetchPatientClinicalSummaryByPatientId(patientId);
    }
}
