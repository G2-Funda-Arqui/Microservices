package pe.edu.upc.medibridge.appointments.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.domain.model.entities.FamilyPatientRelation;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.FamilyMemberAssignedToPatientIntegrationEvent;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.FamilyPatientRelationRepository;

@Component
public class FamilyMemberAssignedToPatientEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyMemberAssignedToPatientEventHandler.class);
    private final FamilyPatientRelationRepository familyPatientRelationRepository;

    public FamilyMemberAssignedToPatientEventHandler(FamilyPatientRelationRepository familyPatientRelationRepository) {
        this.familyPatientRelationRepository = familyPatientRelationRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_FAMILY_ASSIGNED_PATIENT)
    public void on(FamilyMemberAssignedToPatientIntegrationEvent event) {
        if (!familyPatientRelationRepository.existsByLinkId(event.linkId())) {
            familyPatientRelationRepository.save(new FamilyPatientRelation(
                    event.linkId(),
                    event.familyMemberProfileId(),
                    event.patientId()));
        }
        LOGGER.info("Family-patient relation received by appointments: familyMemberProfileId={}, patientId={}",
                event.familyMemberProfileId(), event.patientId());
    }
}
