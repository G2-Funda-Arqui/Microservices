package pe.edu.upc.medibridge.appointments.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientInPeriodQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentQueryService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentContextFacade {
    private final AppointmentQueryService appointmentQueryService;

    public AppointmentContextFacade(AppointmentQueryService appointmentQueryService) {
        this.appointmentQueryService = appointmentQueryService;
    }

    public String fetchAppointmentSummaryByPatientId(Long patientId) {
        var appointments = appointmentQueryService.handle(new GetAppointmentsByPatientQuery(patientId));
        return summarizeAppointments(appointments, "No appointments registered for this patient.");
    }

    public String fetchAppointmentSummaryByPatientIdAndPeriod(Long patientId, LocalDate startDate, LocalDate endDate) {
        var appointments = appointmentQueryService.handle(
                new GetAppointmentsByPatientInPeriodQuery(patientId, startDate, endDate));
        return summarizeAppointments(appointments, "No appointments registered for this patient in the report period.");
    }

    private String summarizeAppointments(List<Appointment> appointments, String emptyMessage) {
        if (appointments.isEmpty()) {
            return emptyMessage;
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
