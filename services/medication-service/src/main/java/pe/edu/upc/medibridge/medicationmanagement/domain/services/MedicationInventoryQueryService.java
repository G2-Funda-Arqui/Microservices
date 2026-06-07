package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetLowStockMedicationsQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationByIdQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationsByPatientQuery;

import java.util.List;
import java.util.Optional;

public interface MedicationInventoryQueryService {
    Optional<Medication> handle(GetMedicationByIdQuery query);
    List<Medication> handle(GetMedicationsByPatientQuery query);
    List<Medication> handle(GetLowStockMedicationsQuery query);
}
