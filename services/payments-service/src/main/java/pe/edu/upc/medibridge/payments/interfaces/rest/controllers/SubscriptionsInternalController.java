package pe.edu.upc.medibridge.payments.interfaces.rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetActiveSubscriptionQuery;
import pe.edu.upc.medibridge.payments.domain.services.SubscriptionQueryService;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.SubscriptionResponse;
import pe.edu.upc.medibridge.payments.interfaces.rest.transform.SubscriptionResponseFromEntityAssembler;

@RestController
@RequestMapping("/api/v1/internal/subscriptions")
public class SubscriptionsInternalController {
    private final SubscriptionQueryService subscriptionQueryService;

    public SubscriptionsInternalController(SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionQueryService = subscriptionQueryService;
    }

    @GetMapping("/users/{userId}/active")
    public ResponseEntity<SubscriptionResponse> getActiveSubscription(@PathVariable Long userId) {
        return subscriptionQueryService.handle(new GetActiveSubscriptionQuery(userId))
                .map(value -> ResponseEntity.ok(SubscriptionResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
