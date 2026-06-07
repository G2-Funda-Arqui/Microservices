package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.acl.HealthMonitoringContextFacade;

@RestController
@RequestMapping("/api/v1/internal/health-monitoring")
public class HealthMonitoringInternalController {
    private final HealthMonitoringContextFacade healthMonitoringContextFacade;

    public HealthMonitoringInternalController(HealthMonitoringContextFacade healthMonitoringContextFacade) {
        this.healthMonitoringContextFacade = healthMonitoringContextFacade;
    }

    @GetMapping("/patients/{patientId}/summary")
    public String getPatientHealthSummary(@PathVariable Long patientId) {
        return healthMonitoringContextFacade.fetchPatientClinicalSummaryByPatientId(patientId);
    }
}
