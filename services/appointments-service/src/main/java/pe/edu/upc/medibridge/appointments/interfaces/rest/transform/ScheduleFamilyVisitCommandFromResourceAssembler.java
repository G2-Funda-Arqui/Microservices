package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleFamilyVisitCommand;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.ScheduleFamilyVisitResource;

public class ScheduleFamilyVisitCommandFromResourceAssembler {
    public static ScheduleFamilyVisitCommand toCommandFromResource(ScheduleFamilyVisitResource resource, Long requestedByUserId) {
        return new ScheduleFamilyVisitCommand(
                resource.patientId(),
                resource.familyMemberProfileId(),
                resource.startsAt(),
                resource.durationInMinutes(),
                resource.reason(),
                requestedByUserId);
    }
}
