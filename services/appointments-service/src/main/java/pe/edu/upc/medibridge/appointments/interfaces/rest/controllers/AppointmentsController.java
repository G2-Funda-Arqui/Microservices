package pe.edu.upc.medibridge.appointments.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentByIdQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentCommandService;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentQueryService;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.AppointmentResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.FamilyVisitResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.MedicalAppointmentResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.ScheduleFamilyVisitResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.ScheduleMedicalAppointmentResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.AppointmentResourceFromEntityAssembler;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.FamilyVisitResourceFromEntityAssembler;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.MedicalAppointmentResourceFromEntityAssembler;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.ScheduleFamilyVisitCommandFromResourceAssembler;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.ScheduleMedicalAppointmentCommandFromResourceAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Appointments", description = "Appointment Scheduling Endpoints")
public class AppointmentsController {

    private final AppointmentCommandService appointmentCommandService;
    private final AppointmentQueryService appointmentQueryService;

    public AppointmentsController(
            AppointmentCommandService appointmentCommandService,
            AppointmentQueryService appointmentQueryService) {
        this.appointmentCommandService = appointmentCommandService;
        this.appointmentQueryService = appointmentQueryService;
    }

    @PostMapping(value = "/family-visits", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FamilyVisitResource> scheduleFamilyVisit(
            @RequestBody ScheduleFamilyVisitResource resource) {
        var command = ScheduleFamilyVisitCommandFromResourceAssembler.toCommandFromResource(resource);
        var appointment = appointmentCommandService.handle(command);

        if (appointment.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var appointmentResource = FamilyVisitResourceFromEntityAssembler.toResourceFromEntity(appointment.get());
        return new ResponseEntity<>(appointmentResource, HttpStatus.CREATED);
    }

    @PostMapping(value = "/medical", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicalAppointmentResource> scheduleMedicalAppointment(
            @RequestBody ScheduleMedicalAppointmentResource resource) {
        var command = ScheduleMedicalAppointmentCommandFromResourceAssembler.toCommandFromResource(resource);
        var appointment = appointmentCommandService.handle(command);

        if (appointment.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var appointmentResource = MedicalAppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment.get());
        return new ResponseEntity<>(appointmentResource, HttpStatus.CREATED);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResource> getAppointmentById(@PathVariable Long appointmentId) {
        var appointment = appointmentQueryService.handle(new GetAppointmentByIdQuery(appointmentId));

        if (appointment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var appointmentResource = AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment.get());
        return ResponseEntity.ok(appointmentResource);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResource>> getAppointmentsByPatient(@PathVariable Long patientId) {
        var appointments = appointmentQueryService.handle(new GetAppointmentsByPatientQuery(patientId));
        var appointmentResources = appointments.stream()
                .map(AppointmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(appointmentResources);
    }
}
