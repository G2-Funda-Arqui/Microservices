package pe.edu.upc.medibridge.medicationmanagement.application.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetDoseAdministrationHistoryQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationQueryService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.DoseAdministrationRepository;

import java.util.List;

@Service
public class DoseAdministrationQueryServiceImpl implements DoseAdministrationQueryService {
    private final DoseAdministrationRepository doseAdministrationRepository;

    public DoseAdministrationQueryServiceImpl(DoseAdministrationRepository doseAdministrationRepository) {
        this.doseAdministrationRepository = doseAdministrationRepository;
    }

    @Override
    public List<DoseAdministration> handle(GetDoseAdministrationHistoryQuery query) {
        return doseAdministrationRepository.findByMedicationIdOrderByOccurredAtDesc(query.medicationId());
    }
}
