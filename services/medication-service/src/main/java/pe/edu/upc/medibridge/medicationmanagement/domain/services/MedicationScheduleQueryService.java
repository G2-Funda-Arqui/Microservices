package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetActiveMedicationSchedulesQuery;

import java.util.List;

public interface MedicationScheduleQueryService {
    List<MedicationSchedule> handle(GetActiveMedicationSchedulesQuery query);
}
