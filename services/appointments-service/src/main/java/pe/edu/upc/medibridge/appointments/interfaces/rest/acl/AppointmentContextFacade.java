package pe.edu.upc.medibridge.appointments.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentQueryService;

import java.util.stream.Collectors;

@Service
public class AppointmentContextFacade {
    private final AppointmentQueryService appointmentQueryService;

    public AppointmentContextFacade(AppointmentQueryService appointmentQueryService) {
        this.appointmentQueryService = appointmentQueryService;
    }

    public String fetchAppointmentSummaryByPatientId(Long patientId) {
        var appointments = appointmentQueryService.handle(new GetAppointmentsByPatientQuery(patientId));
        if (appointments.isEmpty()) {
            return "No appointments registered for this patient.";
        }
        return appointments.stream()
                .map(appointment -> {
                    var timeSlot = appointment.getTimeSlot();
                    var professional = appointment.getDoctorProfileId() != null
                            ? "assigned doctor"
                            : "family member";
                    var reason = appointment.getReason() == null || appointment.getReason().isBlank()
                            ? "No reason registered"
                            : appointment.getReason();
                    return appointment.getAppointmentType()
                            + " appointment with " + professional
                            + " from " + timeSlot.getStartsAt()
                            + " to " + timeSlot.getEndsAt()
                            + ". Status: " + appointment.getStatus()
                            + ". Reason: " + reason + ".";
                })
                .collect(Collectors.joining(" "));
    }
}
