package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.CreateMedicationScheduleCommand;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.CreateMedicationScheduleRequest;

public class CreateMedicationScheduleCommandFromResourceAssembler {
    public static CreateMedicationScheduleCommand toCommandFromResource(CreateMedicationScheduleRequest resource, Long requestedByUserId) {
        return new CreateMedicationScheduleCommand(
                resource.medicationId(),
                resource.patientId(),
                resource.frequencyType(),
                resource.timesPerDay(),
                resource.administrationTime(),
                resource.startDate(),
                resource.endDate(),
                requestedByUserId);
    }
}
