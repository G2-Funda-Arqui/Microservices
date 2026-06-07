package pe.edu.upc.medibridge.healthmonitoring.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.entities.DoctorPatientRelation;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.events.DoctorAssignedToPatientIntegrationEvent;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.DoctorPatientRelationRepository;

@Component
public class DoctorAssignedToPatientEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorAssignedToPatientEventHandler.class);
    private final DoctorPatientRelationRepository doctorPatientRelationRepository;

    public DoctorAssignedToPatientEventHandler(DoctorPatientRelationRepository doctorPatientRelationRepository) {
        this.doctorPatientRelationRepository = doctorPatientRelationRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_DOCTOR_ASSIGNED_PATIENT)
    public void on(DoctorAssignedToPatientIntegrationEvent event) {
        if (!doctorPatientRelationRepository.existsByAssignmentId(event.assignmentId())) {
            doctorPatientRelationRepository.save(new DoctorPatientRelation(
                    event.assignmentId(),
                    event.doctorProfileId(),
                    event.patientId()));
        }
        LOGGER.info("Doctor-patient relation received by health monitoring: doctorProfileId={}, patientId={}",
                event.doctorProfileId(), event.patientId());
    }
}
