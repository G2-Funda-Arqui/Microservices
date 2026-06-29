package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.MedicationServiceClient;

import java.time.LocalDate;

@Service
public class MedicationExternalService implements ExternalMedicationService {
    private final MedicationServiceClient medicationServiceClient;

    public MedicationExternalService(MedicationServiceClient medicationServiceClient) {
        this.medicationServiceClient = medicationServiceClient;
    }

    @Override
    @CircuitBreaker(name = "medicationService", fallbackMethod = "getMedicationSummaryFallback")
    public String getMedicationSummary(Long patientId, LocalDate startDate, LocalDate endDate) {
        var summary = medicationServiceClient.getMedicationSummary(patientId, startDate, endDate);
        if (summary.activeMedications() == 0) {
            return "No active medications registered for this patient.";
        }
        return "Medication summary: " + summary.activeMedications() + " active medications, "
                + summary.lowStockMedications() + " low-stock medications, "
                + summary.doseAdministrations() + " dose administrations recorded in the report period.";
    }

    private String getMedicationSummaryFallback(Long patientId, LocalDate startDate, LocalDate endDate, Throwable exception) {
        return "Medication summary is temporarily unavailable.";
    }
}
