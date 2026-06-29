package pe.edu.upc.medibridge.iam.infrastructure.messaging.publishers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.iam.domain.model.exceptions.IntegrationEventPublishingException;
import pe.edu.upc.medibridge.iam.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.iam.infrastructure.messaging.events.UserRegisteredIntegrationEvent;

@Component
public class UserIntegrationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public UserIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishUserRegisteredFallback")
    public void publishUserRegistered(Long userId, String username) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_USER_REGISTERED,
                new UserRegisteredIntegrationEvent(userId, username));
    }

    private void publishUserRegisteredFallback(Long userId, String username, Throwable exception) {
        throw new IntegrationEventPublishingException("RabbitMQ user registered publishing failed", exception);
    }
}
