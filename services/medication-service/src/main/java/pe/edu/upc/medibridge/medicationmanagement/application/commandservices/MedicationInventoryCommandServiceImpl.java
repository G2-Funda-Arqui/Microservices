package pe.edu.upc.medibridge.medicationmanagement.application.commandservices;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl.ExternalPatientContextService;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RegisterMedicationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.UpdateMedicationStockCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.MedicationExpiredEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.MedicationRegisteredEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.StockCriticallyLowEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationNotFoundException;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationInventoryCommandService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.publishers.MedicationIntegrationEventPublisher;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;

import java.util.Optional;

@Service
public class MedicationInventoryCommandServiceImpl implements MedicationInventoryCommandService {
    private final MedicationRepository medicationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ExternalPatientContextService externalPatientContextService;
    private final MedicationIntegrationEventPublisher integrationEventPublisher;

    public MedicationInventoryCommandServiceImpl(
            MedicationRepository medicationRepository,
            ApplicationEventPublisher eventPublisher,
            ExternalPatientContextService externalPatientContextService,
            MedicationIntegrationEventPublisher integrationEventPublisher) {
        this.medicationRepository = medicationRepository;
        this.eventPublisher = eventPublisher;
        this.externalPatientContextService = externalPatientContextService;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<Medication> handle(RegisterMedicationCommand command) {
        if (!externalPatientContextService.patientExists(command.patientId())) {
            throw new InvalidPatientReferenceException(command.patientId());
        }
        var medication = medicationRepository.save(new Medication(command));
        eventPublisher.publishEvent(new MedicationRegisteredEvent(medication.getId(), medication.getPatientId()));
        integrationEventPublisher.publishMedicationRegistered(medication);
        if (medication.isLowStock()) {
            eventPublisher.publishEvent(new StockCriticallyLowEvent(medication.getId(), medication.getPatientId(), medication.getStockQuantity()));
            integrationEventPublisher.publishStockLow(medication);
        }
        if (medication.isExpired()) {
            eventPublisher.publishEvent(new MedicationExpiredEvent(medication.getId(), medication.getPatientId()));
        }
        return Optional.of(medication);
    }

    @Override
    public Optional<Medication> handle(UpdateMedicationStockCommand command) {
        var medication = medicationRepository.findById(command.medicationId())
                .orElseThrow(() -> new MedicationNotFoundException(command.medicationId()));
        medication.updateStock(command.stockQuantity());
        var updatedMedication = medicationRepository.save(medication);
        if (updatedMedication.isLowStock()) {
            eventPublisher.publishEvent(new StockCriticallyLowEvent(
                    updatedMedication.getId(),
                    updatedMedication.getPatientId(),
                    updatedMedication.getStockQuantity()));
            integrationEventPublisher.publishStockLow(updatedMedication);
        }
        return Optional.of(updatedMedication);
    }
}
