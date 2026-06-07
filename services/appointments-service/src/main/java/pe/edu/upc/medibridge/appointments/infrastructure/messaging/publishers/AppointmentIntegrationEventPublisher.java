package pe.edu.upc.medibridge.appointments.infrastructure.messaging.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.AppointmentScheduledIntegrationEvent;

@Component
public class AppointmentIntegrationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public AppointmentIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAppointmentScheduled(Appointment appointment) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_APPOINTMENT_SCHEDULED,
                new AppointmentScheduledIntegrationEvent(
                        appointment.getId(),
                        appointment.getPatientId(),
                        appointment.getDoctorProfileId(),
                        appointment.getFamilyMemberProfileId(),
                        appointment.getAppointmentType().name(),
                        appointment.getTimeSlot().getStartsAt(),
                        appointment.getTimeSlot().getEndsAt()));
    }
}
