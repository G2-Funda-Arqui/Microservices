package pe.edu.upc.medibridge.healthmonitoring.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.acl.resources.SubscriptionResponse;

@FeignClient(name = "payments-service", url = "${services.payments.url}", path = "/api/v1/internal/subscriptions")
public interface PaymentsServiceClient {
    @GetMapping("/users/{userId}/active")
    SubscriptionResponse getActiveSubscriptionByUser(@PathVariable Long userId);
}
