package pe.edu.upc.medibridge.payments.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.payments.domain.model.aggregates.Invoice;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetInvoiceHistoryQuery;
import pe.edu.upc.medibridge.payments.domain.services.InvoiceQueryService;
import pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories.InvoiceRepository;

import java.util.List;

@Service
public class InvoiceQueryServiceImpl implements InvoiceQueryService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceQueryServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<Invoice> handle(GetInvoiceHistoryQuery query) {
        return invoiceRepository.findByUserIdOrderByIssuedAtDesc(query.userId());
    }
}
