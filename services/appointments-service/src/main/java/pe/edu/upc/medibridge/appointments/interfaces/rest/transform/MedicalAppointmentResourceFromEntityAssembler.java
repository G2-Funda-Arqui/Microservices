package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.MedicalAppointmentResource;

public class MedicalAppointmentResourceFromEntityAssembler {

    public static MedicalAppointmentResource toResourceFromEntity(Appointment appointment) {
        return new MedicalAppointmentResource(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorProfileId(),
                appointment.getAppointmentType().name(),
                appointment.getStatus().name(),
                appointment.getTimeSlot().getStartsAt(),
                appointment.getTimeSlot().getEndsAt(),
                appointment.getReason());
    }
}
