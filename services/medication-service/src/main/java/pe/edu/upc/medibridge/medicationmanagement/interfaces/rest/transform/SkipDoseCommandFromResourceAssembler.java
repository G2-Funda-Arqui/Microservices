package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.SkipDoseCommand;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.SkipDoseRequest;

public class SkipDoseCommandFromResourceAssembler {
    public static SkipDoseCommand toCommandFromResource(SkipDoseRequest resource) {
        return new SkipDoseCommand(
                resource.medicationId(),
                resource.scheduleId(),
                resource.patientId(),
                resource.skippedAt(),
                resource.reason());
    }
}
