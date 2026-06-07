package pe.edu.upc.medibridge.payments.interfaces.rest.transform;

import pe.edu.upc.medibridge.payments.domain.model.aggregates.Invoice;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.InvoiceResponse;

public class InvoiceResponseFromEntityAssembler {
    public static InvoiceResponse toResourceFromEntity(Invoice entity) {
        return new InvoiceResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getSubscriptionId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getIssuedAt());
    }
}
