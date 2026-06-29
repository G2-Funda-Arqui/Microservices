package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RecordDoseAdministrationCommand;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.RecordDoseAdministrationRequest;

public class RecordDoseAdministrationCommandFromResourceAssembler {
    public static RecordDoseAdministrationCommand toCommandFromResource(RecordDoseAdministrationRequest resource, Long requestedByUserId) {
        return new RecordDoseAdministrationCommand(
                resource.medicationId(),
                resource.scheduleId(),
                resource.patientId(),
                resource.administeredAt(),
                resource.notes(),
                requestedByUserId);
    }
}
