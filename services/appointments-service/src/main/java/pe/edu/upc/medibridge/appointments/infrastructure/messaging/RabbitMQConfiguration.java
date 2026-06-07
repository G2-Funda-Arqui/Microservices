package pe.edu.upc.medibridge.appointments.infrastructure.messaging;

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
    public static final String ROUTING_KEY_DOCTOR_ASSIGNED_PATIENT = "doctor.assigned.patient";
    public static final String ROUTING_KEY_FAMILY_ASSIGNED_PATIENT = "family.assigned.patient";
    public static final String ROUTING_KEY_APPOINTMENT_SCHEDULED = "appointment.scheduled";

    public static final String QUEUE_PATIENT_REGISTERED = "appointments.patient-registered";
    public static final String QUEUE_PATIENT_DEACTIVATED = "appointments.patient-deactivated";
    public static final String QUEUE_DOCTOR_ASSIGNED_PATIENT = "appointments.doctor-assigned-patient";
    public static final String QUEUE_FAMILY_ASSIGNED_PATIENT = "appointments.family-assigned-patient";

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
    public Queue doctorAssignedPatientQueue() {
        return durableQueue(QUEUE_DOCTOR_ASSIGNED_PATIENT);
    }

    @Bean
    public Queue familyAssignedPatientQueue() {
        return durableQueue(QUEUE_FAMILY_ASSIGNED_PATIENT);
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
    public Binding doctorAssignedPatientBinding(Queue doctorAssignedPatientQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(doctorAssignedPatientQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_DOCTOR_ASSIGNED_PATIENT);
    }

    @Bean
    public Binding familyAssignedPatientBinding(Queue familyAssignedPatientQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(familyAssignedPatientQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_FAMILY_ASSIGNED_PATIENT);
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
