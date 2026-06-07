package pe.edu.upc.medibridge.payments.domain.model.commands;

import pe.edu.upc.medibridge.payments.domain.model.valueobjects.BillingCycle;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.CommercialLine;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.PlanType;

public record CreateSubscriptionCommand(Long userId, CommercialLine commercialLine, PlanType planType, BillingCycle billingCycle) {
}
