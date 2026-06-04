package pe.edu.upc.medibridge.profiles.interfaces.rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.profiles.domain.model.queries.CanDoctorAttendPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.model.queries.CanFamilyMemberAccessPatientQuery;
import pe.edu.upc.medibridge.profiles.domain.model.queries.GetPatientProfileByIdQuery;
import pe.edu.upc.medibridge.profiles.domain.services.CareRelationshipQueryService;
import pe.edu.upc.medibridge.profiles.domain.services.PatientProfileQueryService;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.PatientProfileResource;
import pe.edu.upc.medibridge.profiles.interfaces.rest.transform.PatientProfileResourceFromEntityAssembler;

@RestController
@RequestMapping("/api/v1/internal/profiles")
public class ProfilesInternalController {
    private final PatientProfileQueryService patientProfileQueryService;
    private final CareRelationshipQueryService careRelationshipQueryService;

    public ProfilesInternalController(
            PatientProfileQueryService patientProfileQueryService,
            CareRelationshipQueryService careRelationshipQueryService) {
        this.patientProfileQueryService = patientProfileQueryService;
        this.careRelationshipQueryService = careRelationshipQueryService;
    }

    @GetMapping("/patients/{patientId}/exists")
    public boolean existsPatientById(@PathVariable Long patientId) {
        return patientId != null && patientProfileQueryService
                .handle(new GetPatientProfileByIdQuery(patientId))
                .isPresent();
    }

    @GetMapping("/patients/{patientId}")
    public PatientProfileResource getPatientProfileById(@PathVariable Long patientId) {
        return patientProfileQueryService.handle(new GetPatientProfileByIdQuery(patientId))
                .map(PatientProfileResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(PatientProfileNotFoundException::new);
    }

    @GetMapping("/doctors/{doctorId}/can-attend/{patientId}")
    public boolean canDoctorAttendPatient(
            @PathVariable Long doctorId,
            @PathVariable Long patientId) {
        return careRelationshipQueryService.handle(new CanDoctorAttendPatientQuery(doctorId, patientId));
    }

    @GetMapping("/family-members/{familyMemberId}/can-visit/{patientId}")
    public boolean canFamilyMemberVisitPatient(
            @PathVariable Long familyMemberId,
            @PathVariable Long patientId) {
        return careRelationshipQueryService.handle(new CanFamilyMemberAccessPatientQuery(familyMemberId, patientId));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class PatientProfileNotFoundException extends RuntimeException {
    }
}