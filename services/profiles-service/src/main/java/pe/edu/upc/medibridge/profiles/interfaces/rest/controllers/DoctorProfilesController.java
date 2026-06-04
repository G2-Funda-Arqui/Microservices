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
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetDoctorProfileByIdQuery;
import pe.edu.upc.medibridge.profiles.domain.services.DoctorProfileCommandService;
import pe.edu.upc.medibridge.profiles.domain.services.DoctorProfileQueryService;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.CreateDoctorProfileResource;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.DoctorProfileResource;
import pe.edu.upc.medibridge.profiles.interfaces.rest.transform.CreateDoctorProfileCommandFromResourceAssembler;
import pe.edu.upc.medibridge.profiles.interfaces.rest.transform.DoctorProfileResourceFromEntityAssembler;

@RestController
@RequestMapping(value = "/api/v1/profiles/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Doctor Profiles", description = "Doctor Profile Management Endpoints")
public class DoctorProfilesController {

    private final DoctorProfileCommandService doctorProfileCommandService;
    private final DoctorProfileQueryService doctorProfileQueryService;

    public DoctorProfilesController(
            DoctorProfileCommandService doctorProfileCommandService,
            DoctorProfileQueryService doctorProfileQueryService) {
        this.doctorProfileCommandService = doctorProfileCommandService;
        this.doctorProfileQueryService = doctorProfileQueryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DoctorProfileResource> createDoctorProfile(
            @RequestBody CreateDoctorProfileResource resource) {
        var command = CreateDoctorProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        var doctorProfile = doctorProfileCommandService.handle(command);

        if (doctorProfile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var doctorProfileResource = DoctorProfileResourceFromEntityAssembler.toResourceFromEntity(doctorProfile.get());
        return new ResponseEntity<>(doctorProfileResource, HttpStatus.CREATED);
    }

    @GetMapping("/{doctorProfileId}")
    public ResponseEntity<DoctorProfileResource> getDoctorProfileById(@PathVariable Long doctorProfileId) {
        var doctorProfile = doctorProfileQueryService.handle(new GetDoctorProfileByIdQuery(doctorProfileId));

        if (doctorProfile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var doctorProfileResource = DoctorProfileResourceFromEntityAssembler.toResourceFromEntity(doctorProfile.get());
        return ResponseEntity.ok(doctorProfileResource);
    }
}
