package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.HealthMonitoringServiceClient;

import java.time.LocalDate;

@Service
public class HealthMonitoringExternalService implements ExternalHealthMonitoringService {
    private final HealthMonitoringServiceClient healthMonitoringServiceClient;

    public HealthMonitoringExternalService(HealthMonitoringServiceClient healthMonitoringServiceClient) {
        this.healthMonitoringServiceClient = healthMonitoringServiceClient;
    }

    @Override
    @CircuitBreaker(name = "healthMonitoringService", fallbackMethod = "getPatientClinicalSummaryFallback")
    public String getPatientClinicalSummary(Long patientId, LocalDate startDate, LocalDate endDate) {
        return healthMonitoringServiceClient.getPatientHealthSummary(patientId, startDate, endDate);
    }

    private String getPatientClinicalSummaryFallback(Long patientId, LocalDate startDate, LocalDate endDate, Throwable exception) {
        return "Health monitoring summary is temporarily unavailable.";
    }
}
