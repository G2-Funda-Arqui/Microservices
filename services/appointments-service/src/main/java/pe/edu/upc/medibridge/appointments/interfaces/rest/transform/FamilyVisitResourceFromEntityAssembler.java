package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.FamilyVisitResource;

public class FamilyVisitResourceFromEntityAssembler {

    public static FamilyVisitResource toResourceFromEntity(Appointment appointment) {
        return new FamilyVisitResource(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getFamilyMemberProfileId(),
                appointment.getAppointmentType().name(),
                appointment.getStatus().name(),
                appointment.getTimeSlot().getStartsAt(),
                appointment.getTimeSlot().getEndsAt(),
                appointment.getReason());
    }
}
