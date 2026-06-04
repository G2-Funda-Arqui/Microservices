package pe.edu.upc.medibridge.profiles.infrastructure.messaging;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    public static final String EXCHANGE = "medibridge.events";
    public static final String DLX = "medibridge.dlx";

    public static final String ROUTING_KEY_PATIENT_REGISTERED = "patient.registered";
    public static final String ROUTING_KEY_PATIENT_DEACTIVATED = "patient.deactivated";
    public static final String ROUTING_KEY_DOCTOR_ASSIGNED_PATIENT = "doctor.assigned.patient";
    public static final String ROUTING_KEY_FAMILY_ASSIGNED_PATIENT = "family.assigned.patient";

    @Bean
    public TopicExchange medibridgeEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX).durable(true).build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setExchange(EXCHANGE);
        return template;
    }
}