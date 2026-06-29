package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.reportsanalytics.application.internal.queryservices.AuthenticatedPatientAccessService;
import pe.edu.upc.medibridge.reportsanalytics.application.internal.queryservices.PremiumAccessService;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.commands.GeneratePdfReportCommand;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetReportByIdQuery;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.queries.GetReportsByPatientQuery;
import pe.edu.upc.medibridge.reportsanalytics.domain.services.ClinicalReportCommandService;
import pe.edu.upc.medibridge.reportsanalytics.domain.services.ClinicalReportQueryService;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.pdf.ITextPdfReportGenerator;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.pdf.PdfReportStorage;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources.ClinicalReportResponse;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources.GenerateReportRequest;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.transform.ClinicalReportResponseFromEntityAssembler;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.transform.GenerateReportCommandFromResourceAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/clinical-reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Clinical Reports", description = "Clinical Report Generation Endpoints")
public class ClinicalReportController {
    private final ClinicalReportCommandService clinicalReportCommandService;
    private final ClinicalReportQueryService clinicalReportQueryService;
    private final ITextPdfReportGenerator pdfReportGenerator;
    private final PdfReportStorage pdfReportStorage;
    private final PremiumAccessService premiumAccessService;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public ClinicalReportController(
            ClinicalReportCommandService clinicalReportCommandService,
            ClinicalReportQueryService clinicalReportQueryService,
            ITextPdfReportGenerator pdfReportGenerator,
            PdfReportStorage pdfReportStorage,
            PremiumAccessService premiumAccessService,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.clinicalReportCommandService = clinicalReportCommandService;
        this.clinicalReportQueryService = clinicalReportQueryService;
        this.pdfReportGenerator = pdfReportGenerator;
        this.pdfReportStorage = pdfReportStorage;
        this.premiumAccessService = premiumAccessService;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    @PostMapping
    public ResponseEntity<ClinicalReportResponse> generateReport(
            @RequestBody GenerateReportRequest resource,
            @AuthenticationPrincipal Jwt jwt) {
        premiumAccessService.requirePaidSubscription(jwt, "clinical reports");
        var requestedByUserId = authenticatedPatientAccessService.resolveUserId(jwt);
        var command = GenerateReportCommandFromResourceAssembler.toCommandFromResource(resource, requestedByUserId);
        var report = clinicalReportCommandService.handle(command);
        return report
                .map(value -> new ResponseEntity<>(
                        ClinicalReportResponseFromEntityAssembler.toResourceFromEntity(value),
                        HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/{reportId}/pdf")
    public ResponseEntity<ClinicalReportResponse> generatePdf(
            @PathVariable Integer reportId,
            @AuthenticationPrincipal Jwt jwt) {
        premiumAccessService.requirePaidSubscription(jwt, "PDF report generation");
        var requestedByUserId = authenticatedPatientAccessService.resolveUserId(jwt);
        var report = clinicalReportCommandService.handle(new GeneratePdfReportCommand(reportId, requestedByUserId));
        return report
                .map(value -> ResponseEntity.ok(ClinicalReportResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{reportId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Integer reportId,
            @AuthenticationPrincipal Jwt jwt) {
        premiumAccessService.requirePaidSubscription(jwt, "PDF report downloads");
        var requestedByUserId = authenticatedPatientAccessService.resolveUserId(jwt);
        var report = clinicalReportQueryService.handle(new GetReportByIdQuery(reportId, requestedByUserId));
        if (report.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var clinicalReport = report.get();
        var pdf = pdfReportStorage.readPdf(clinicalReport.getPdfPath())
                .orElseGet(() -> pdfReportGenerator.generate(clinicalReport));
        var filename = "clinical-report-" + reportId + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ClinicalReportResponse> getReportById(
            @PathVariable Integer reportId,
            @AuthenticationPrincipal Jwt jwt) {
        premiumAccessService.requirePaidSubscription(jwt, "clinical reports");
        var requestedByUserId = authenticatedPatientAccessService.resolveUserId(jwt);
        var report = clinicalReportQueryService.handle(new GetReportByIdQuery(reportId, requestedByUserId));
        return report
                .map(value -> ResponseEntity.ok(ClinicalReportResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<List<ClinicalReportResponse>> getReportsByPatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal Jwt jwt) {
        premiumAccessService.requirePaidSubscription(jwt, "clinical reports");
        var requestedByUserId = authenticatedPatientAccessService.resolveUserId(jwt);
        var reports = clinicalReportQueryService.handle(new GetReportsByPatientQuery(patientId, requestedByUserId));
        var resources = reports.stream()
                .map(ClinicalReportResponseFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
