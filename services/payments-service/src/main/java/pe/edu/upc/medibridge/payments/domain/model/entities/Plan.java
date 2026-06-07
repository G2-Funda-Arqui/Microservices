package pe.edu.upc.medibridge.payments.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.BillingCycle;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.CommercialLine;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.PlanType;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "plans")
@NoArgsConstructor
public class Plan extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CommercialLine commercialLine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillingCycle billingCycle;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 120)
    private String displayName;

    @Column(nullable = false)
    private Integer maxPatients;

    @Column(nullable = false)
    private boolean active;

    public Plan(
            CommercialLine commercialLine,
            PlanType planType,
            BillingCycle billingCycle,
            BigDecimal price,
            String currency,
            String displayName,
            Integer maxPatients) {
        this.commercialLine = commercialLine;
        this.planType = planType;
        this.billingCycle = billingCycle;
        this.price = price;
        this.currency = currency;
        this.displayName = displayName;
        this.maxPatients = maxPatients;
        this.active = true;
    }
}
