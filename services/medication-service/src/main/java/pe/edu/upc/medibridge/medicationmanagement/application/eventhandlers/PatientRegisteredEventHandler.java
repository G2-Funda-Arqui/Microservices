package pe.edu.upc.medibridge.medicationmanagement.application.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.PatientReference;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.PatientRegisteredIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Component
public class PatientRegisteredEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRegisteredEventHandler.class);

    private final PatientReferenceRepository patientReferenceRepository;

    public PatientRegisteredEventHandler(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PATIENT_REGISTERED)
    public void on(PatientRegisteredIntegrationEvent event) {
        patientReferenceRepository.findByPatientId(event.patientId())
                .ifPresentOrElse(
                        reference -> {
                            reference.reactivate(event.fullName());
                            patientReferenceRepository.save(reference);
                        },
                        () -> patientReferenceRepository.save(new PatientReference(event.patientId(), event.fullName())));
        LOGGER.info("Patient reference synchronized for medication service: patientId={}", event.patientId());
    }
}
