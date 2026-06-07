package pe.edu.upc.medibridge.appointments.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.PatientDeactivatedIntegrationEvent;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Component
public class PatientDeactivatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDeactivatedEventHandler.class);
    private final PatientReferenceRepository patientReferenceRepository;

    public PatientDeactivatedEventHandler(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PATIENT_DEACTIVATED)
    public void on(PatientDeactivatedIntegrationEvent event) {
        patientReferenceRepository.findByPatientId(event.patientId()).ifPresent(patient -> {
            patient.deactivate();
            patientReferenceRepository.save(patient);
        });
        LOGGER.info("Patient deactivation received by appointments: patientId={}", event.patientId());
    }
}
