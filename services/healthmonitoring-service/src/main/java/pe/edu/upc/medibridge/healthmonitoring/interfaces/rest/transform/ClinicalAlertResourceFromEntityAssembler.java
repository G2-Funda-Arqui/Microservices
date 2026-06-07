package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.transform;

import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.ClinicalAlert;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources.ClinicalAlertResource;

public class ClinicalAlertResourceFromEntityAssembler {

    public static ClinicalAlertResource toResourceFromEntity(ClinicalAlert entity) {
        return new ClinicalAlertResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getObservationId(),
                entity.getSeverity(),
                entity.getStatus(),
                entity.getMessage(),
                entity.getTriggeredAt());
    }
}
