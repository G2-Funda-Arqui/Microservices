package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.resources;

import java.time.LocalDate;

public record SubscriptionResponse(
        Long id,
        Long userId,
        PlanResponse plan,
        String status,
        String stripeCustomerId,
        LocalDate startedAt,
        LocalDate currentPeriodEnd) {
}
