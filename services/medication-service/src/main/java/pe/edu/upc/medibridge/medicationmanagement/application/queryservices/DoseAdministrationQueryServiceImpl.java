package pe.edu.upc.medibridge.medicationmanagement.application.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationNotFoundException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetDoseAdministrationHistoryQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationQueryService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.DoseAdministrationRepository;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;

import java.util.List;

@Service
public class DoseAdministrationQueryServiceImpl implements DoseAdministrationQueryService {
    private final DoseAdministrationRepository doseAdministrationRepository;
    private final MedicationRepository medicationRepository;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public DoseAdministrationQueryServiceImpl(
            DoseAdministrationRepository doseAdministrationRepository,
            MedicationRepository medicationRepository,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.doseAdministrationRepository = doseAdministrationRepository;
        this.medicationRepository = medicationRepository;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    @Override
    public List<DoseAdministration> handle(GetDoseAdministrationHistoryQuery query) {
        var medication = medicationRepository.findById(query.medicationId())
                .orElseThrow(() -> new MedicationNotFoundException(query.medicationId()));
        authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), medication.getPatientId());
        return doseAdministrationRepository.findByMedicationIdOrderByOccurredAtDesc(query.medicationId());
    }
}
