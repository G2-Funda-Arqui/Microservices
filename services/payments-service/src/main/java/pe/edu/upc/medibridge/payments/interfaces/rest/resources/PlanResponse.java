package pe.edu.upc.medibridge.payments.interfaces.rest.resources;

import pe.edu.upc.medibridge.payments.domain.model.valueobjects.BillingCycle;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.CommercialLine;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.PlanType;

import java.math.BigDecimal;

public record PlanResponse(
        Integer id,
        CommercialLine commercialLine,
        PlanType planType,
        BillingCycle billingCycle,
        BigDecimal price,
        String currency,
        String displayName,
        Integer maxPatients) {
}
