package pe.edu.upc.medibridge.communication.infrastructure.messaging;

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

    public static final String ROUTING_KEY_ALERT_CRITICAL_TRIGGERED = "alert.critical.triggered";
    public static final String ROUTING_KEY_DOSE_ADMINISTERED = "dose.administered";
    public static final String ROUTING_KEY_DOSE_SKIPPED = "dose.skipped";
    public static final String ROUTING_KEY_STOCK_LOW = "stock.low";

    public static final String QUEUE_ALERT_CRITICAL_TRIGGERED = "communication.alert-critical";
    public static final String QUEUE_DOSE_ADMINISTERED = "communication.dose-administered";
    public static final String QUEUE_DOSE_SKIPPED = "communication.dose-skipped";
    public static final String QUEUE_STOCK_LOW = "communication.stock-low";

    @Bean
    public TopicExchange medibridgeEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue alertCriticalTriggeredQueue() {
        return durableQueue(QUEUE_ALERT_CRITICAL_TRIGGERED);
    }

    @Bean
    public Queue doseAdministeredQueue() {
        return durableQueue(QUEUE_DOSE_ADMINISTERED);
    }

    @Bean
    public Queue doseSkippedQueue() {
        return durableQueue(QUEUE_DOSE_SKIPPED);
    }

    @Bean
    public Queue stockLowQueue() {
        return durableQueue(QUEUE_STOCK_LOW);
    }

    @Bean
    public Binding alertCriticalTriggeredBinding(Queue alertCriticalTriggeredQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(alertCriticalTriggeredQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_ALERT_CRITICAL_TRIGGERED);
    }

    @Bean
    public Binding doseAdministeredBinding(Queue doseAdministeredQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(doseAdministeredQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_DOSE_ADMINISTERED);
    }

    @Bean
    public Binding doseSkippedBinding(Queue doseSkippedQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(doseSkippedQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_DOSE_SKIPPED);
    }

    @Bean
    public Binding stockLowBinding(Queue stockLowQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(stockLowQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_STOCK_LOW);
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
