package pe.edu.upc.medibridge.payments.infrastructure.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.payments.application.internal.outboundservices.acl.StripePaymentGatewayService;
import pe.edu.upc.medibridge.payments.domain.model.entities.Plan;
import pe.edu.upc.medibridge.payments.domain.model.exceptions.PaymentProcessingException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class StripePaymentGatewayAdapter implements StripePaymentGatewayService {
    public StripePaymentGatewayAdapter(@Value("${stripe.secret.key}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    @CircuitBreaker(name = "stripeApi", fallbackMethod = "createCustomerFallback")
    public String createCustomer(Long userId) {
        try {
            var params = CustomerCreateParams.builder()
                    .putMetadata("medibridge_user_id", String.valueOf(userId))
                    .build();
            Customer customer = Customer.create(params);
            return customer.getId();
        } catch (StripeException exception) {
            throw new PaymentProcessingException("Stripe customer creation failed: " + exception.getMessage());
        }
    }

    @Override
    @CircuitBreaker(name = "stripeApi", fallbackMethod = "createPaymentIntentFallback")
    public String createPaymentIntent(Long userId, Plan plan) {
        try {
            var params = PaymentIntentCreateParams.builder()
                    .setAmount(toMinorCurrencyUnit(plan.getPrice()))
                    .setCurrency(plan.getCurrency().toLowerCase())
                    .setDescription("MediBridge " + plan.getPlanType() + " " + plan.getBillingCycle() + " subscription")
                    .putMetadata("medibridge_user_id", String.valueOf(userId))
                    .putMetadata("plan_type", plan.getPlanType().name())
                    .putMetadata("billing_cycle", plan.getBillingCycle().name())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return paymentIntent.getId();
        } catch (StripeException exception) {
            throw new PaymentProcessingException("Stripe payment intent creation failed: " + exception.getMessage());
        }
    }

    private String createCustomerFallback(Long userId, Throwable exception) {
        throw new PaymentProcessingException("Stripe customer creation circuit breaker fallback: " + exception.getMessage());
    }

    private String createPaymentIntentFallback(Long userId, Plan plan, Throwable exception) {
        throw new PaymentProcessingException("Stripe payment intent circuit breaker fallback: " + exception.getMessage());
    }

    private Long toMinorCurrencyUnit(BigDecimal amount) {
        return amount
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}
