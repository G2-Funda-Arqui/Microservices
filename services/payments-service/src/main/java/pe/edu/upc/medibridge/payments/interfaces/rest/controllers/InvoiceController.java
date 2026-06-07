package pe.edu.upc.medibridge.payments.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetInvoiceHistoryQuery;
import pe.edu.upc.medibridge.payments.domain.services.InvoiceQueryService;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.InvoiceResponse;
import pe.edu.upc.medibridge.payments.interfaces.rest.transform.InvoiceResponseFromEntityAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Invoices", description = "Invoice Query Endpoints")
public class InvoiceController {
    private final InvoiceQueryService invoiceQueryService;

    public InvoiceController(InvoiceQueryService invoiceQueryService) {
        this.invoiceQueryService = invoiceQueryService;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoiceHistory(@PathVariable Long userId) {
        var invoices = invoiceQueryService.handle(new GetInvoiceHistoryQuery(userId));
        var resources = invoices.stream()
                .map(InvoiceResponseFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
