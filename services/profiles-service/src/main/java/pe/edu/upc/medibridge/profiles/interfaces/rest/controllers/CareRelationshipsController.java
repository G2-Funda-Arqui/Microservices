package pe.edu.upc.medibridge.profiles.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl.ExternalIamContextService;
import pe.edu.upc.medibridge.profiles.domain.model.commands.AssignDoctorToPatientCommand;
import pe.edu.upc.medibridge.profiles.domain.model.commands.LinkFamilyMemberToPatientCommand;
import pe.edu.upc.medibridge.profiles.domain.model.exceptions.InvalidProfileRequestException;
import pe.edu.upc.medibridge.profiles.domain.services.CareRelationshipCommandService;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.DoctorPatientAssignmentResource;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.FamilyPatientLinkResource;
import pe.edu.upc.medibridge.profiles.interfaces.rest.transform.DoctorPatientAssignmentResourceFromEntityAssembler;
import pe.edu.upc.medibridge.profiles.interfaces.rest.transform.FamilyPatientLinkResourceFromEntityAssembler;

@RestController
@RequestMapping(value = "/api/v1/profiles/patients/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Care Relationships", description = "Patient Care Relationship Endpoints")
public class CareRelationshipsController {

    private final CareRelationshipCommandService careRelationshipCommandService;
    private final ExternalIamContextService externalIamContextService;

    public CareRelationshipsController(
            CareRelationshipCommandService careRelationshipCommandService,
            ExternalIamContextService externalIamContextService) {
        this.careRelationshipCommandService = careRelationshipCommandService;
        this.externalIamContextService = externalIamContextService;
    }

    @PostMapping("/doctors/{doctorProfileId}")
    public ResponseEntity<DoctorPatientAssignmentResource> assignDoctorToPatient(
            @PathVariable Long patientId,
            @PathVariable Long doctorProfileId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = resolveAuthenticatedUserId(jwt);
        var assignment = careRelationshipCommandService.handle(
                new AssignDoctorToPatientCommand(doctorProfileId, patientId, userId));

        if (assignment.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var resource = DoctorPatientAssignmentResourceFromEntityAssembler
                .toResourceFromEntity(assignment.get());
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @PostMapping("/family-members/{familyMemberProfileId}")
    public ResponseEntity<FamilyPatientLinkResource> linkFamilyMemberToPatient(
            @PathVariable Long patientId,
            @PathVariable Long familyMemberProfileId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = resolveAuthenticatedUserId(jwt);
        var link = careRelationshipCommandService.handle(
                new LinkFamilyMemberToPatientCommand(familyMemberProfileId, patientId, userId));

        if (link.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var resource = FamilyPatientLinkResourceFromEntityAssembler.toResourceFromEntity(link.get());
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    private Long resolveAuthenticatedUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new InvalidProfileRequestException("Authenticated user is required");
        }
        return externalIamContextService.findUserIdByUsername(jwt.getSubject())
                .orElseThrow(() -> new InvalidProfileRequestException("Authenticated user was not found"));
    }
}
