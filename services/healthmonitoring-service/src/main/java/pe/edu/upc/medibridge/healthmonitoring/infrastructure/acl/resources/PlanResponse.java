package pe.edu.upc.medibridge.healthmonitoring.infrastructure.acl.resources;

import java.math.BigDecimal;

public record PlanResponse(
        Integer id,
        String commercialLine,
        String planType,
        String billingCycle,
        BigDecimal price,
        String currency,
        String displayName,
        Integer maxPatients) {
}
