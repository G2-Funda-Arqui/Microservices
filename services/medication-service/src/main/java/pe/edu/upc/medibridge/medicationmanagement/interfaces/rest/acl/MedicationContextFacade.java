package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationsByPatientQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationInventoryQueryService;

import java.util.stream.Collectors;

@Service
public class MedicationContextFacade {
    private final MedicationInventoryQueryService medicationInventoryQueryService;

    public MedicationContextFacade(MedicationInventoryQueryService medicationInventoryQueryService) {
        this.medicationInventoryQueryService = medicationInventoryQueryService;
    }

    public String fetchMedicationSummaryByPatientId(Long patientId) {
        var medications = medicationInventoryQueryService.handle(new GetMedicationsByPatientQuery(patientId));
        if (medications.isEmpty()) {
            return "No active medications registered for this patient.";
        }
        return medications.stream()
                .map(medication -> {
                    var stockStatus = medication.isLowStock()
                            ? "low stock"
                            : "stock available";
                    var expirationStatus = medication.isExpired()
                            ? "expired"
                            : "expires on " + medication.getExpirationDate();
                    return medication.getName()
                            + ": " + medication.getDosageAmount().stripTrailingZeros().toPlainString()
                            + " " + medication.getDosageUnit()
                            + " via " + medication.getAdministrationRoute()
                            + ". Stock: " + medication.getStockQuantity()
                            + " units, threshold: " + medication.getLowStockThreshold()
                            + " (" + stockStatus + "). " + expirationStatus + ".";
                })
                .collect(Collectors.joining(" "));
    }
}
