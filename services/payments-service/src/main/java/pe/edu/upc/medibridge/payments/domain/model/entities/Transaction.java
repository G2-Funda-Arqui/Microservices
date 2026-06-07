package pe.edu.upc.medibridge.payments.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "transactions")
@NoArgsConstructor
public class Transaction extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, unique = true, length = 120)
    private String stripePaymentIntentId;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    public Transaction(Long userId, BigDecimal amount, String currency, String stripePaymentIntentId, String status) {
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}
