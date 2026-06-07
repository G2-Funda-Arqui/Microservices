package pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.payments.domain.model.entities.Plan;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.BillingCycle;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.CommercialLine;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.PlanType;
import pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories.PlanRepository;

import java.math.BigDecimal;

@Component
public class PaymentsDataInitializer implements CommandLineRunner {
    private final PlanRepository planRepository;

    public PaymentsDataInitializer(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) {
        seedPlan(CommercialLine.FAMILY, PlanType.FREE, BillingCycle.MONTHLY, BigDecimal.ZERO, "Plan Gratuito Familiar", 1);
        seedPlan(CommercialLine.FAMILY, PlanType.FAMILY_PREMIUM, BillingCycle.MONTHLY, new BigDecimal("19.90"), "Plan Premium Familiar", 3);
        seedPlan(CommercialLine.FAMILY, PlanType.FAMILY_PREMIUM, BillingCycle.ANNUALLY, new BigDecimal("199.00"), "Plan Premium Familiar Anual", 3);

        seedPlan(CommercialLine.INSTITUTION, PlanType.INSTITUTION_BASIC, BillingCycle.MONTHLY, new BigDecimal("149.00"), "Plan Institucional Basic", 50);
        seedPlan(CommercialLine.INSTITUTION, PlanType.INSTITUTION_PREMIUM, BillingCycle.MONTHLY, new BigDecimal("299.00"), "Plan Institucional Premium", 200);
        seedPlan(CommercialLine.INSTITUTION, PlanType.INSTITUTION_PREMIUM, BillingCycle.ANNUALLY, new BigDecimal("2990.00"), "Plan Institucional Premium Anual", 200);
    }

    private void seedPlan(
            CommercialLine commercialLine,
            PlanType planType,
            BillingCycle billingCycle,
            BigDecimal price,
            String displayName,
            Integer maxPatients) {
        if (!planRepository.existsByCommercialLineAndPlanTypeAndBillingCycle(commercialLine, planType, billingCycle)) {
            planRepository.save(new Plan(commercialLine, planType, billingCycle, price, "USD", displayName, maxPatients));
        }
    }
}
