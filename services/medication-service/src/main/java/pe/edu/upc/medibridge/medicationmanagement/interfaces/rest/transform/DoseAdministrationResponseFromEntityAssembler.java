package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.DoseAdministrationResponse;

public class DoseAdministrationResponseFromEntityAssembler {
    public static DoseAdministrationResponse toResourceFromEntity(DoseAdministration entity) {
        return new DoseAdministrationResponse(
                entity.getId(),
                entity.getMedicationId(),
                entity.getScheduleId(),
                entity.getPatientId(),
                entity.getOccurredAt(),
                entity.getStatus(),
                entity.getNotes());
    }
}
