package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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
    public static final String ROUTING_KEY_MEDICATION_REGISTERED = "medication.registered";
    public static final String ROUTING_KEY_DOSE_ADMINISTERED = "dose.administered";
    public static final String ROUTING_KEY_DOSE_SKIPPED = "dose.skipped";
    public static final String ROUTING_KEY_STOCK_LOW = "stock.low";

    public static final String QUEUE_PATIENT_REGISTERED = "medication.patient-registered";
    public static final String QUEUE_PATIENT_DEACTIVATED = "medication.patient-deactivated";

    @Bean
    public TopicExchange medibridgeEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue patientRegisteredQueue() {
        return durableQueue(QUEUE_PATIENT_REGISTERED);
    }

    @Bean
    public Queue patientDeactivatedQueue() {
        return durableQueue(QUEUE_PATIENT_DEACTIVATED);
    }

    @Bean
    public Binding patientRegisteredBinding(Queue patientRegisteredQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(patientRegisteredQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_PATIENT_REGISTERED);
    }

    @Bean
    public Binding patientDeactivatedBinding(Queue patientDeactivatedQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(patientDeactivatedQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_PATIENT_DEACTIVATED);
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

    private Queue durableQueue(String name) {
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", DLX)
                .build();
    }
}
