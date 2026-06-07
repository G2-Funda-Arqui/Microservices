package pe.edu.upc.medibridge.medicationmanagement.application.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.PatientDeactivatedIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Component
public class PatientDeactivatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDeactivatedEventHandler.class);

    private final PatientReferenceRepository patientReferenceRepository;

    public PatientDeactivatedEventHandler(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PATIENT_DEACTIVATED)
    public void on(PatientDeactivatedIntegrationEvent event) {
        patientReferenceRepository.findByPatientId(event.patientId())
                .ifPresent(reference -> {
                    reference.deactivate();
                    patientReferenceRepository.save(reference);
                });
        LOGGER.info("Patient reference deactivated for medication service: patientId={}", event.patientId());
    }
}
