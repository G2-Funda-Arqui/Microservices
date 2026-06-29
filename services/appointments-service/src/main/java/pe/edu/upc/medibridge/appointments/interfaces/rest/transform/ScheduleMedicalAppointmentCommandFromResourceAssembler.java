package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleMedicalAppointmentCommand;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.ScheduleMedicalAppointmentResource;

public class ScheduleMedicalAppointmentCommandFromResourceAssembler {
    public static ScheduleMedicalAppointmentCommand toCommandFromResource(ScheduleMedicalAppointmentResource resource, Long requestedByUserId) {
        return new ScheduleMedicalAppointmentCommand(
                resource.patientId(),
                resource.doctorProfileId(),
                resource.startsAt(),
                resource.durationInMinutes(),
                resource.reason(),
                requestedByUserId);
    }
}
