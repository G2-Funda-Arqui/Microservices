package pe.edu.upc.medibridge.payments.interfaces.rest.resources;

import pe.edu.upc.medibridge.payments.domain.model.valueobjects.SubscriptionStatus;

import java.time.LocalDate;

public record SubscriptionResponse(
        Long id,
        Long userId,
        PlanResponse plan,
        SubscriptionStatus status,
        String stripeCustomerId,
        LocalDate startedAt,
        LocalDate currentPeriodEnd) {
}
