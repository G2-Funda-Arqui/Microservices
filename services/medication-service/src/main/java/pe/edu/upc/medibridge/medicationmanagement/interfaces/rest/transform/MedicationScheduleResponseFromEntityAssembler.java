package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationScheduleResponse;

public class MedicationScheduleResponseFromEntityAssembler {
    public static MedicationScheduleResponse toResourceFromEntity(MedicationSchedule entity) {
        return new MedicationScheduleResponse(
                entity.getId(),
                entity.getMedicationId(),
                entity.getPatientId(),
                entity.getFrequencyType(),
                entity.getTimesPerDay(),
                entity.getAdministrationTime(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isActive());
    }
}
