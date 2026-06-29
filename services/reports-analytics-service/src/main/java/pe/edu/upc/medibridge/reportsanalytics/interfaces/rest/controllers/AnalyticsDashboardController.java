package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.reportsanalytics.application.internal.queryservices.AuthenticatedPatientAccessService;
import pe.edu.upc.medibridge.reportsanalytics.application.internal.queryservices.PremiumAccessService;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetAnalyticsDashboardQuery;
import pe.edu.upc.medibridge.reportsanalytics.domain.services.AnalyticsDashboardQueryService;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources.DashboardMetricsResponse;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.transform.DashboardMetricsResponseFromEntityAssembler;

@RestController
@RequestMapping(value = "/api/v1/analytics-dashboards", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics Dashboards", description = "Analytics Dashboard Endpoints")
public class AnalyticsDashboardController {
    private final AnalyticsDashboardQueryService analyticsDashboardQueryService;
    private final PremiumAccessService premiumAccessService;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public AnalyticsDashboardController(
            AnalyticsDashboardQueryService analyticsDashboardQueryService,
            PremiumAccessService premiumAccessService,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.analyticsDashboardQueryService = analyticsDashboardQueryService;
        this.premiumAccessService = premiumAccessService;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<DashboardMetricsResponse> getDashboardByPatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal Jwt jwt) {
        premiumAccessService.requirePaidSubscription(jwt, "analytics dashboards");
        var requestedByUserId = authenticatedPatientAccessService.resolveUserId(jwt);
        var dashboard = analyticsDashboardQueryService.handle(new GetAnalyticsDashboardQuery(patientId, requestedByUserId));
        return ResponseEntity.ok(DashboardMetricsResponseFromEntityAssembler.toResourceFromEntity(dashboard));
    }
}
