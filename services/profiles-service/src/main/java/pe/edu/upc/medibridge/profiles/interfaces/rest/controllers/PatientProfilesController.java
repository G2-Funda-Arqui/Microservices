package pe.edu.upc.medibridge.profiles.interfaces.rest.controllers;

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
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetPatientProfileByIdQuery;
import pe.edu.upc.medibridge.profiles.domain.services.PatientProfileCommandService;
import pe.edu.upc.medibridge.profiles.domain.services.PatientProfileQueryService;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.CreatePatientProfileResource;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.PatientProfileResource;
import pe.edu.upc.medibridge.profiles.interfaces.rest.transform.CreatePatientProfileCommandFromResourceAssembler;
import pe.edu.upc.medibridge.profiles.interfaces.rest.transform.PatientProfileResourceFromEntityAssembler;

@RestController
@RequestMapping(value = "/api/v1/profiles/patients", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Patient Profiles", description = "Patient Profile Management Endpoints")
public class PatientProfilesController {

    private final PatientProfileCommandService patientProfileCommandService;
    private final PatientProfileQueryService patientProfileQueryService;

    public PatientProfilesController(
            PatientProfileCommandService patientProfileCommandService,
            PatientProfileQueryService patientProfileQueryService) {
        this.patientProfileCommandService = patientProfileCommandService;
        this.patientProfileQueryService = patientProfileQueryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientProfileResource> createPatientProfile(
            @RequestBody CreatePatientProfileResource resource) {
        var command = CreatePatientProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        var patientProfile = patientProfileCommandService.handle(command);

        if (patientProfile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var patientProfileResource = PatientProfileResourceFromEntityAssembler
                .toResourceFromEntity(patientProfile.get());
        return new ResponseEntity<>(patientProfileResource, HttpStatus.CREATED);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientProfileResource> getPatientProfileById(@PathVariable Long patientId) {
        var patientProfile = patientProfileQueryService.handle(new GetPatientProfileByIdQuery(patientId));

        if (patientProfile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var patientProfileResource = PatientProfileResourceFromEntityAssembler
                .toResourceFromEntity(patientProfile.get());
        return ResponseEntity.ok(patientProfileResource);
    }
}
