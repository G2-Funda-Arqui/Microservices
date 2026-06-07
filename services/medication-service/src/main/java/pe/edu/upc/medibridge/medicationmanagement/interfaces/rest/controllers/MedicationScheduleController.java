package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetActiveMedicationSchedulesQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationScheduleCommandService;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationScheduleQueryService;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.CreateMedicationScheduleRequest;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationScheduleResponse;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.CreateMedicationScheduleCommandFromResourceAssembler;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.MedicationScheduleResponseFromEntityAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/medication-schedules", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Medication Schedules", description = "Medication Schedule Management Endpoints")
public class MedicationScheduleController {
    private final MedicationScheduleCommandService medicationScheduleCommandService;
    private final MedicationScheduleQueryService medicationScheduleQueryService;

    public MedicationScheduleController(
            MedicationScheduleCommandService medicationScheduleCommandService,
            MedicationScheduleQueryService medicationScheduleQueryService) {
        this.medicationScheduleCommandService = medicationScheduleCommandService;
        this.medicationScheduleQueryService = medicationScheduleQueryService;
    }

    @PostMapping
    public ResponseEntity<MedicationScheduleResponse> createSchedule(@RequestBody CreateMedicationScheduleRequest resource) {
        var command = CreateMedicationScheduleCommandFromResourceAssembler.toCommandFromResource(resource);
        var schedule = medicationScheduleCommandService.handle(command);
        return schedule
                .map(value -> new ResponseEntity<>(
                        MedicationScheduleResponseFromEntityAssembler.toResourceFromEntity(value),
                        HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/patients/{patientId}/active")
    public ResponseEntity<List<MedicationScheduleResponse>> getActiveSchedulesByPatient(@PathVariable Long patientId) {
        var schedules = medicationScheduleQueryService.handle(new GetActiveMedicationSchedulesQuery(patientId));
        var resources = schedules.stream()
                .map(MedicationScheduleResponseFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
