package pe.edu.upc.medibridge.payments.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.payments.domain.model.commands.CancelSubscriptionCommand;
import pe.edu.upc.medibridge.payments.domain.model.commands.RenewSubscriptionCommand;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetActiveSubscriptionQuery;
import pe.edu.upc.medibridge.payments.domain.model.queries.GetSubscriptionByUserQuery;
import pe.edu.upc.medibridge.payments.domain.services.PaymentMethodCommandService;
import pe.edu.upc.medibridge.payments.domain.services.SubscriptionCommandService;
import pe.edu.upc.medibridge.payments.domain.services.SubscriptionQueryService;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.AddPaymentMethodRequest;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.CreateSubscriptionRequest;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.PaymentMethodResponse;
import pe.edu.upc.medibridge.payments.interfaces.rest.resources.SubscriptionResponse;
import pe.edu.upc.medibridge.payments.interfaces.rest.transform.AddPaymentMethodCommandFromResourceAssembler;
import pe.edu.upc.medibridge.payments.interfaces.rest.transform.CreateSubscriptionCommandFromResourceAssembler;
import pe.edu.upc.medibridge.payments.interfaces.rest.transform.PaymentMethodResponseFromEntityAssembler;
import pe.edu.upc.medibridge.payments.interfaces.rest.transform.SubscriptionResponseFromEntityAssembler;

@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Subscription Management Endpoints")
public class SubscriptionController {
    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final PaymentMethodCommandService paymentMethodCommandService;

    public SubscriptionController(
            SubscriptionCommandService subscriptionCommandService,
            SubscriptionQueryService subscriptionQueryService,
            PaymentMethodCommandService paymentMethodCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
        this.paymentMethodCommandService = paymentMethodCommandService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(@RequestBody CreateSubscriptionRequest resource) {
        var command = CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);
        var subscription = subscriptionCommandService.handle(command);
        return subscription
                .map(value -> new ResponseEntity<>(SubscriptionResponseFromEntityAssembler.toResourceFromEntity(value), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(@PathVariable Long subscriptionId) {
        var subscription = subscriptionCommandService.handle(new CancelSubscriptionCommand(subscriptionId));
        return subscription
                .map(value -> ResponseEntity.ok(SubscriptionResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{subscriptionId}/renew")
    public ResponseEntity<SubscriptionResponse> renewSubscription(@PathVariable Long subscriptionId) {
        var subscription = subscriptionCommandService.handle(new RenewSubscriptionCommand(subscriptionId));
        return subscription
                .map(value -> ResponseEntity.ok(SubscriptionResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionByUser(@PathVariable Long userId) {
        var subscription = subscriptionQueryService.handle(new GetSubscriptionByUserQuery(userId));
        return subscription
                .map(value -> ResponseEntity.ok(SubscriptionResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/active")
    public ResponseEntity<SubscriptionResponse> getActiveSubscription(@PathVariable Long userId) {
        var subscription = subscriptionQueryService.handle(new GetActiveSubscriptionQuery(userId));
        return subscription
                .map(value -> ResponseEntity.ok(SubscriptionResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/payment-methods")
    public ResponseEntity<PaymentMethodResponse> addPaymentMethod(@RequestBody AddPaymentMethodRequest resource) {
        var command = AddPaymentMethodCommandFromResourceAssembler.toCommandFromResource(resource);
        var paymentMethod = paymentMethodCommandService.handle(command);
        return paymentMethod
                .map(value -> new ResponseEntity<>(PaymentMethodResponseFromEntityAssembler.toResourceFromEntity(value), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
