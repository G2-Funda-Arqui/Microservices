package pe.edu.upc.medibridge.payments.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.payments.domain.model.entities.Plan;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.SubscriptionStatus;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "subscriptions")
@NoArgsConstructor
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {
    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubscriptionStatus status;

    @Column(nullable = false, unique = true, length = 120)
    private String stripeCustomerId;

    @Column(nullable = false)
    private LocalDate startedAt;

    @Column(nullable = false)
    private LocalDate currentPeriodEnd;

    public Subscription(Long userId, Plan plan, String stripeCustomerId, LocalDate startedAt, LocalDate currentPeriodEnd) {
        this.userId = userId;
        this.plan = plan;
        this.stripeCustomerId = stripeCustomerId;
        this.startedAt = startedAt;
        this.currentPeriodEnd = currentPeriodEnd;
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }

    public void renew(LocalDate newPeriodEnd) {
        this.status = SubscriptionStatus.ACTIVE;
        this.currentPeriodEnd = newPeriodEnd;
    }

    public boolean isActive() {
        return SubscriptionStatus.ACTIVE.equals(status) || SubscriptionStatus.TRIALING.equals(status);
    }
}
