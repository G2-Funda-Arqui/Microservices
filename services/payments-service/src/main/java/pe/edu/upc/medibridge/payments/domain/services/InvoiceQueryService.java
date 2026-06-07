package pe.edu.upc.medibridge.payments.domain.services;

import pe.edu.upc.medibridge.payments.domain.model.aggregates.Invoice;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetInvoiceHistoryQuery;

import java.util.List;

public interface InvoiceQueryService {
    List<Invoice> handle(GetInvoiceHistoryQuery query);
}
