package pe.edu.upc.medibridge.payments.infrastructure.messaging.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.payments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.payments.infrastructure.messaging.events.SubscriptionActivatedIntegrationEvent;

@Component
public class PaymentIntegrationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public PaymentIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishSubscriptionActivated(Long userId, Long subscriptionId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_SUBSCRIPTION_ACTIVATED,
                new SubscriptionActivatedIntegrationEvent(userId, subscriptionId));
    }
}
