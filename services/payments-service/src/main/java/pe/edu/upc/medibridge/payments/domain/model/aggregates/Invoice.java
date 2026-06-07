package pe.edu.upc.medibridge.payments.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.InvoiceStatus;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "invoices")
@NoArgsConstructor
public class Invoice extends AuditableAbstractAggregateRoot<Invoice> {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long subscriptionId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    public Invoice(Long userId, Long subscriptionId, BigDecimal amount, String currency, InvoiceStatus status) {
        this.userId = userId;
        this.subscriptionId = subscriptionId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.issuedAt = LocalDateTime.now();
    }
}
