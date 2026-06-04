package pe.edu.upc.medibridge.iam.infrastructure.messaging.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.iam.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.iam.infrastructure.messaging.events.UserRegisteredIntegrationEvent;

@Component
public class UserIntegrationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public UserIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserRegistered(Long userId, String username) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_USER_REGISTERED,
                new UserRegisteredIntegrationEvent(userId, username));
    }
}
