package pe.edu.upc.medibridge.medicationmanagement.application.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetLowStockMedicationsQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationByIdQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationsByPatientQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationInventoryQueryService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MedicationInventoryQueryServiceImpl implements MedicationInventoryQueryService {
    private final MedicationRepository medicationRepository;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public MedicationInventoryQueryServiceImpl(
            MedicationRepository medicationRepository,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.medicationRepository = medicationRepository;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    @Override
    public Optional<Medication> handle(GetMedicationByIdQuery query) {
        var medication = medicationRepository.findById(query.medicationId());
        medication.ifPresent(value -> authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), value.getPatientId()));
        return medication;
    }

    @Override
    public List<Medication> handle(GetMedicationsByPatientQuery query) {
        if (query.requestedByUserId() != null) {
            authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), query.patientId());
        }
        return medicationRepository.findByPatientIdAndActiveTrue(query.patientId());
    }

    @Override
    public List<Medication> handle(GetLowStockMedicationsQuery query) {
        authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), query.patientId());
        return medicationRepository.findByPatientIdAndActiveTrue(query.patientId()).stream()
                .filter(Medication::isLowStock)
                .toList();
    }
}
