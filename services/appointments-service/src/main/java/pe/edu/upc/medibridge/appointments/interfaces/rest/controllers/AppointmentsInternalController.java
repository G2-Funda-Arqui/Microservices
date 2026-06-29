package pe.edu.upc.medibridge.appointments.interfaces.rest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.appointments.interfaces.rest.acl.AppointmentContextFacade;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/internal/appointments")
public class AppointmentsInternalController {
    private final AppointmentContextFacade appointmentContextFacade;

    public AppointmentsInternalController(AppointmentContextFacade appointmentContextFacade) {
        this.appointmentContextFacade = appointmentContextFacade;
    }

    @GetMapping("/patients/{patientId}/summary")
    public String getAppointmentSummaryByPatientId(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return appointmentContextFacade.fetchAppointmentSummaryByPatientIdAndPeriod(patientId, startDate, endDate);
        }
        return appointmentContextFacade.fetchAppointmentSummaryByPatientId(patientId);
    }
}
