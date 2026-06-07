package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.MedicationServiceClient;

@Service
public class MedicationExternalService implements ExternalMedicationService {
    private final MedicationServiceClient medicationServiceClient;

    public MedicationExternalService(MedicationServiceClient medicationServiceClient) {
        this.medicationServiceClient = medicationServiceClient;
    }

    @Override
    public String getMedicationSummary(Long patientId) {
        var summary = medicationServiceClient.getMedicationSummary(patientId);
        if (summary.activeMedications() == 0) {
            return "No active medications registered for this patient.";
        }
        return "Medication summary: " + summary.activeMedications() + " active medications, "
                + summary.lowStockMedications() + " low-stock medications, "
                + summary.doseAdministrations() + " dose administrations recorded.";
    }
}
