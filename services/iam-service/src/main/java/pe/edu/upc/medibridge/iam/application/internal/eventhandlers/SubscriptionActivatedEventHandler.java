package pe.edu.upc.medibridge.iam.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.iam.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.iam.infrastructure.messaging.events.SubscriptionActivatedIntegrationEvent;

@Component
public class SubscriptionActivatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionActivatedEventHandler.class);

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_SUBSCRIPTION_ACTIVATED)
    public void on(SubscriptionActivatedIntegrationEvent event) {
        LOGGER.info("Subscription activation event received by IAM: userId={}, subscriptionId={}, status={}",
                event.userId(), event.subscriptionId(), event.status());
    }
}
