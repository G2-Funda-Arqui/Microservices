package pe.edu.upc.medibridge.payments.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.payments.domain.model.entities.UserReference;
import pe.edu.upc.medibridge.payments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.payments.infrastructure.messaging.events.UserRegisteredIntegrationEvent;
import pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories.UserReferenceRepository;

@Component
public class UserRegisteredEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegisteredEventHandler.class);
    private final UserReferenceRepository userReferenceRepository;

    public UserRegisteredEventHandler(UserReferenceRepository userReferenceRepository) {
        this.userReferenceRepository = userReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_USER_REGISTERED)
    public void on(UserRegisteredIntegrationEvent event) {
        if (!userReferenceRepository.existsByUserId(event.userId())) {
            userReferenceRepository.save(new UserReference(event.userId(), event.username()));
        }
        LOGGER.info("User registration event received by payments: userId={}, username={}",
                event.userId(), event.username());
    }
}
