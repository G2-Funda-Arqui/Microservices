package pe.edu.upc.medibridge.healthmonitoring.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.acl.HealthMonitoringContextFacade;

@Service
public class PatientHealthSummaryQueryService {
    private final HealthMonitoringContextFacade healthMonitoringContextFacade;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public PatientHealthSummaryQueryService(
            HealthMonitoringContextFacade healthMonitoringContextFacade,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.healthMonitoringContextFacade = healthMonitoringContextFacade;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    public String getSummary(Long patientId, Long requestedByUserId) {
        authenticatedPatientAccessService.requireAccess(requestedByUserId, patientId);
        return healthMonitoringContextFacade.fetchPatientClinicalSummaryByPatientId(patientId);
    }
}
