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

    public MedicationInventoryQueryServiceImpl(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @Override
    public Optional<Medication> handle(GetMedicationByIdQuery query) {
        return medicationRepository.findById(query.medicationId());
    }

    @Override
    public List<Medication> handle(GetMedicationsByPatientQuery query) {
        return medicationRepository.findByPatientIdAndActiveTrue(query.patientId());
    }

    @Override
    public List<Medication> handle(GetLowStockMedicationsQuery query) {
        return medicationRepository.findByPatientIdAndActiveTrue(query.patientId()).stream()
                .filter(Medication::isLowStock)
                .toList();
    }
}
