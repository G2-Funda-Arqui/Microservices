package pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.payments.domain.model.entities.Plan;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.BillingCycle;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.CommercialLine;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.PlanType;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    Optional<Plan> findByCommercialLineAndPlanTypeAndBillingCycleAndActiveTrue(
            CommercialLine commercialLine,
            PlanType planType,
            BillingCycle billingCycle);

    boolean existsByCommercialLineAndPlanTypeAndBillingCycle(
            CommercialLine commercialLine,
            PlanType planType,
            BillingCycle billingCycle);
}
