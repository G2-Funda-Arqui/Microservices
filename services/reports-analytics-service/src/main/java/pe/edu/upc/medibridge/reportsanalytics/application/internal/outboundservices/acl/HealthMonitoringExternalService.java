package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.HealthMonitoringServiceClient;

@Service
public class HealthMonitoringExternalService implements ExternalHealthMonitoringService {
    private final HealthMonitoringServiceClient healthMonitoringServiceClient;

    public HealthMonitoringExternalService(HealthMonitoringServiceClient healthMonitoringServiceClient) {
        this.healthMonitoringServiceClient = healthMonitoringServiceClient;
    }

    @Override
    public String getPatientClinicalSummary(Long patientId) {
        return healthMonitoringServiceClient.getPatientHealthSummary(patientId);
    }
}
