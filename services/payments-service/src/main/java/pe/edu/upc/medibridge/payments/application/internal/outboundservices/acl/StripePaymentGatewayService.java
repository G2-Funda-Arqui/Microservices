package pe.edu.upc.medibridge.payments.application.internal.outboundservices.acl;

import pe.edu.upc.medibridge.payments.domain.model.entities.Plan;

public interface StripePaymentGatewayService {
    String createCustomer(Long userId);
    String createPaymentIntent(Long userId, Plan plan);
}
