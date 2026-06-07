package pe.edu.upc.medibridge.appointments.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.domain.model.entities.PatientReference;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.PatientRegisteredIntegrationEvent;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Component
public class PatientRegisteredEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRegisteredEventHandler.class);
    private final PatientReferenceRepository patientReferenceRepository;

    public PatientRegisteredEventHandler(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PATIENT_REGISTERED)
    public void on(PatientRegisteredIntegrationEvent event) {
        if (patientReferenceRepository.findByPatientId(event.patientId()).isEmpty()) {
            patientReferenceRepository.save(new PatientReference(event.patientId(), event.fullName()));
        }
        LOGGER.info("Patient reference received by appointments: patientId={}, fullName={}",
                event.patientId(), event.fullName());
    }
}
