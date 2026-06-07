package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.controllers;

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
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetActiveClinicalAlertsByPatientQuery;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetPatientHealthObservationsQuery;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.ClinicalAlertQueryService;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.HealthObservationCommandService;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.HealthObservationQueryService;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.acl.HealthMonitoringContextFacade;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources.ClinicalAlertResource;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources.PatientHealthObservationResource;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources.PatientHealthSummaryResource;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources.RecordPatientHealthObservationResource;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.transform.ClinicalAlertResourceFromEntityAssembler;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.transform.PatientHealthObservationResourceFromEntityAssembler;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.transform.RecordPatientHealthObservationCommandFromResourceAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/health-monitoring/patients/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Health Monitoring", description = "Patient Health Monitoring Endpoints")
public class HealthMonitoringController {

    private final HealthObservationCommandService healthObservationCommandService;
    private final HealthObservationQueryService healthObservationQueryService;
    private final ClinicalAlertQueryService clinicalAlertQueryService;
    private final HealthMonitoringContextFacade healthMonitoringContextFacade;

    public HealthMonitoringController(
            HealthObservationCommandService healthObservationCommandService,
            HealthObservationQueryService healthObservationQueryService,
            ClinicalAlertQueryService clinicalAlertQueryService,
            HealthMonitoringContextFacade healthMonitoringContextFacade) {
        this.healthObservationCommandService = healthObservationCommandService;
        this.healthObservationQueryService = healthObservationQueryService;
        this.clinicalAlertQueryService = clinicalAlertQueryService;
        this.healthMonitoringContextFacade = healthMonitoringContextFacade;
    }

    @PostMapping(value = "/observations", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientHealthObservationResource> recordHealthObservation(
            @PathVariable Long patientId,
            @RequestBody RecordPatientHealthObservationResource resource) {
        var command = RecordPatientHealthObservationCommandFromResourceAssembler
                .toCommandFromResource(patientId, resource);
        var observation = healthObservationCommandService.handle(command);

        if (observation.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var observationResource = PatientHealthObservationResourceFromEntityAssembler
                .toResourceFromEntity(observation.get());
        return new ResponseEntity<>(observationResource, HttpStatus.CREATED);
    }

    @GetMapping("/observations")
    public ResponseEntity<List<PatientHealthObservationResource>> getPatientHealthObservations(
            @PathVariable Long patientId) {
        var observations = healthObservationQueryService.handle(new GetPatientHealthObservationsQuery(patientId));
        var resources = observations.stream()
                .map(PatientHealthObservationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/alerts/active")
    public ResponseEntity<List<ClinicalAlertResource>> getActiveClinicalAlerts(@PathVariable Long patientId) {
        var alerts = clinicalAlertQueryService.handle(new GetActiveClinicalAlertsByPatientQuery(patientId));
        var resources = alerts.stream()
                .map(ClinicalAlertResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/summary")
    public ResponseEntity<PatientHealthSummaryResource> getPatientHealthSummary(@PathVariable Long patientId) {
        var summary = healthMonitoringContextFacade.fetchPatientClinicalSummaryByPatientId(patientId);
        return ResponseEntity.ok(new PatientHealthSummaryResource(patientId, summary));
    }
}
