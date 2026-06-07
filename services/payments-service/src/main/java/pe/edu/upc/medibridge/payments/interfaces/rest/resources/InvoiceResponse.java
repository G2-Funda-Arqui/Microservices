package pe.edu.upc.medibridge.payments.interfaces.rest.resources;

import pe.edu.upc.medibridge.payments.domain.model.valueobjects.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponse(
        Long id,
        Long userId,
        Long subscriptionId,
        BigDecimal amount,
        String currency,
        InvoiceStatus status,
        LocalDateTime issuedAt) {
}
