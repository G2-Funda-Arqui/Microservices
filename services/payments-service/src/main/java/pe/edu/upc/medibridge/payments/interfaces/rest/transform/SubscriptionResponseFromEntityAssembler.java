package pe.edu.upc.medibridge.payments.interfaces.rest.transform;

import pe.edu.upc.medibridge.payments.domain.model.aggregates.Subscription;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.PlanResponse;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.SubscriptionResponse;

public class SubscriptionResponseFromEntityAssembler {
    public static SubscriptionResponse toResourceFromEntity(Subscription entity) {
        var plan = entity.getPlan();
        var planResponse = new PlanResponse(
                plan.getId(),
                plan.getCommercialLine(),
                plan.getPlanType(),
                plan.getBillingCycle(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getDisplayName(),
                plan.getMaxPatients());
        return new SubscriptionResponse(
                entity.getId(),
                entity.getUserId(),
                planResponse,
                entity.getStatus(),
                entity.getStripeCustomerId(),
                entity.getStartedAt(),
                entity.getCurrentPeriodEnd());
    }
}
