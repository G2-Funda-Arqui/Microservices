package pe.edu.upc.medibridge.payments.interfaces.rest.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.payments.domain.model.events.PaymentFailedEvent;

@RestController
@RequestMapping(value = "/api/v1/stripe-webhooks", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Stripe Webhooks", description = "Stripe Webhook Endpoints")
public class StripeWebhookController {
    private final ApplicationEventPublisher eventPublisher;
    private final String webhookSecret;

    public StripeWebhookController(
            ApplicationEventPublisher eventPublisher,
            @Value("${stripe.webhook.secret}") String webhookSecret) {
        this.eventPublisher = eventPublisher;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signatureHeader) {
        if (webhookSecret == null || webhookSecret.isBlank() || signatureHeader == null || signatureHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if ("payment_intent.payment_failed".equals(event.getType())) {
            event.getDataObjectDeserializer().getObject()
                    .filter(PaymentIntent.class::isInstance)
                    .map(PaymentIntent.class::cast)
                    .ifPresent(paymentIntent -> eventPublisher.publishEvent(new PaymentFailedEvent(
                            parseUserId(paymentIntent.getMetadata().get("medibridge_user_id")),
                            paymentIntent.getId())));
        }

        return ResponseEntity.ok().build();
    }

    private Long parseUserId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.valueOf(value);
    }
}
