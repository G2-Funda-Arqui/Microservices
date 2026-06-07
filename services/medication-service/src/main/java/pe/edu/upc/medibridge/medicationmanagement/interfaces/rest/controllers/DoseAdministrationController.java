package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetDoseAdministrationHistoryQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationCommandService;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationQueryService;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.DoseAdministrationResponse;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.RecordDoseAdministrationRequest;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.SkipDoseRequest;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.DoseAdministrationResponseFromEntityAssembler;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.RecordDoseAdministrationCommandFromResourceAssembler;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.SkipDoseCommandFromResourceAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/dose-administrations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dose Administrations", description = "Dose Administration Management Endpoints")
public class DoseAdministrationController {
    private final DoseAdministrationCommandService doseAdministrationCommandService;
    private final DoseAdministrationQueryService doseAdministrationQueryService;

    public DoseAdministrationController(
            DoseAdministrationCommandService doseAdministrationCommandService,
            DoseAdministrationQueryService doseAdministrationQueryService) {
        this.doseAdministrationCommandService = doseAdministrationCommandService;
        this.doseAdministrationQueryService = doseAdministrationQueryService;
    }

    @PostMapping
    public ResponseEntity<DoseAdministrationResponse> recordDoseAdministration(
            @RequestBody RecordDoseAdministrationRequest resource) {
        var command = RecordDoseAdministrationCommandFromResourceAssembler.toCommandFromResource(resource);
        var doseAdministration = doseAdministrationCommandService.handle(command);
        return doseAdministration
                .map(value -> new ResponseEntity<>(
                        DoseAdministrationResponseFromEntityAssembler.toResourceFromEntity(value),
                        HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/skip")
    public ResponseEntity<DoseAdministrationResponse> skipDose(@RequestBody SkipDoseRequest resource) {
        var command = SkipDoseCommandFromResourceAssembler.toCommandFromResource(resource);
        var doseAdministration = doseAdministrationCommandService.handle(command);
        return doseAdministration
                .map(value -> new ResponseEntity<>(
                        DoseAdministrationResponseFromEntityAssembler.toResourceFromEntity(value),
                        HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/medications/{medicationId}")
    public ResponseEntity<List<DoseAdministrationResponse>> getDoseAdministrationHistory(
            @PathVariable Integer medicationId) {
        var doseAdministrations = doseAdministrationQueryService.handle(
                new GetDoseAdministrationHistoryQuery(medicationId));
        var resources = doseAdministrations.stream()
                .map(DoseAdministrationResponseFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
