package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.DoseAdministrationRepository;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationSummaryResource;

@RestController
@RequestMapping(value = "/api/v1/internal/medications", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicationInternalController {
    private final MedicationRepository medicationRepository;
    private final DoseAdministrationRepository doseAdministrationRepository;

    public MedicationInternalController(
            MedicationRepository medicationRepository,
            DoseAdministrationRepository doseAdministrationRepository) {
        this.medicationRepository = medicationRepository;
        this.doseAdministrationRepository = doseAdministrationRepository;
    }

    @GetMapping("/patients/{patientId}/summary")
    public ResponseEntity<MedicationSummaryResource> getMedicationSummary(@PathVariable Long patientId) {
        var activeMedications = medicationRepository.findByPatientIdAndActiveTrue(patientId);
        var lowStockMedications = activeMedications.stream()
                .filter(medication -> medication.getStockQuantity() <= medication.getLowStockThreshold())
                .toList();
        var doseAdministrations = doseAdministrationRepository.countByPatientId(patientId);

        return ResponseEntity.ok(new MedicationSummaryResource(
                patientId,
                activeMedications.size(),
                lowStockMedications.size(),
                doseAdministrations));
    }
}
