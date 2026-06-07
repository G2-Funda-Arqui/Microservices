package pe.edu.upc.medibridge.appointments.interfaces.rest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.appointments.interfaces.rest.acl.AppointmentContextFacade;

@RestController
@RequestMapping("/api/v1/internal/appointments")
public class AppointmentsInternalController {
    private final AppointmentContextFacade appointmentContextFacade;

    public AppointmentsInternalController(AppointmentContextFacade appointmentContextFacade) {
        this.appointmentContextFacade = appointmentContextFacade;
    }

    @GetMapping("/patients/{patientId}/summary")
    public String getAppointmentSummaryByPatientId(@PathVariable Long patientId) {
        return appointmentContextFacade.fetchAppointmentSummaryByPatientId(patientId);
    }
}
