package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetAnalyticsDashboardQuery;
import pe.edu.upc.medibridge.reportsanalytics.domain.services.AnalyticsDashboardQueryService;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources.DashboardMetricsResponse;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.transform.DashboardMetricsResponseFromEntityAssembler;

@RestController
@RequestMapping(value = "/api/v1/analytics-dashboards", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics Dashboards", description = "Analytics Dashboard Endpoints")
public class AnalyticsDashboardController {
    private final AnalyticsDashboardQueryService analyticsDashboardQueryService;

    public AnalyticsDashboardController(AnalyticsDashboardQueryService analyticsDashboardQueryService) {
        this.analyticsDashboardQueryService = analyticsDashboardQueryService;
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<DashboardMetricsResponse> getDashboardByPatient(@PathVariable Long patientId) {
        var dashboard = analyticsDashboardQueryService.handle(new GetAnalyticsDashboardQuery(patientId));
        return ResponseEntity.ok(DashboardMetricsResponseFromEntityAssembler.toResourceFromEntity(dashboard));
    }
}
