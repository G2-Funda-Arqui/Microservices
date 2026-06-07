package pe.edu.upc.medibridge.medicationmanagement.application.commandservices;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RecordDoseAdministrationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.SkipDoseCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ClinicalLog;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.DoseAdministeredEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.DoseSkippedEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.StockCriticallyLowEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.DoseAlreadyAdministeredTodayException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InsufficientStockException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationNotFoundException;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationCommandService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.publishers.MedicationIntegrationEventPublisher;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.ClinicalLogRepository;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.DoseAdministrationRepository;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DoseAdministrationStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class DoseAdministrationCommandServiceImpl implements DoseAdministrationCommandService {
    private final DoseAdministrationRepository doseAdministrationRepository;
    private final MedicationRepository medicationRepository;
    private final ClinicalLogRepository clinicalLogRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MedicationIntegrationEventPublisher integrationEventPublisher;

    public DoseAdministrationCommandServiceImpl(
            DoseAdministrationRepository doseAdministrationRepository,
            MedicationRepository medicationRepository,
            ClinicalLogRepository clinicalLogRepository,
            ApplicationEventPublisher eventPublisher,
            MedicationIntegrationEventPublisher integrationEventPublisher) {
        this.doseAdministrationRepository = doseAdministrationRepository;
        this.medicationRepository = medicationRepository;
        this.clinicalLogRepository = clinicalLogRepository;
        this.eventPublisher = eventPublisher;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<DoseAdministration> handle(RecordDoseAdministrationCommand command) {
        ensureDoseWasNotAdministeredToday(command.scheduleId(), command.administeredAt());
        var medication = medicationRepository.findById(command.medicationId())
                .orElseThrow(() -> new MedicationNotFoundException(command.medicationId()));
        if (medication.getStockQuantity() <= 0) {
            throw new InsufficientStockException(command.medicationId());
        }
        medication.decreaseStock();
        medicationRepository.save(medication);

        var doseAdministration = doseAdministrationRepository.save(new DoseAdministration(command));
        clinicalLogRepository.save(new ClinicalLog(
                command.patientId(),
                command.medicationId(),
                "Dose administered for medication " + medication.getName()));
        eventPublisher.publishEvent(new DoseAdministeredEvent(command.medicationId(), command.scheduleId(), command.patientId()));
        integrationEventPublisher.publishDoseAdministered(command.medicationId(), command.scheduleId(), command.patientId());
        if (medication.isLowStock()) {
            eventPublisher.publishEvent(new StockCriticallyLowEvent(medication.getId(), medication.getPatientId(), medication.getStockQuantity()));
            integrationEventPublisher.publishStockLow(medication);
        }
        return Optional.of(doseAdministration);
    }

    @Override
    public Optional<DoseAdministration> handle(SkipDoseCommand command) {
        var doseAdministration = doseAdministrationRepository.save(new DoseAdministration(command));
        clinicalLogRepository.save(new ClinicalLog(
                command.patientId(),
                command.medicationId(),
                "Dose skipped. Reason: " + command.reason()));
        eventPublisher.publishEvent(new DoseSkippedEvent(command.medicationId(), command.scheduleId(), command.patientId()));
        integrationEventPublisher.publishDoseSkipped(command.medicationId(), command.scheduleId(), command.patientId(), command.reason());
        return Optional.of(doseAdministration);
    }

    private void ensureDoseWasNotAdministeredToday(Integer scheduleId, LocalDateTime occurredAt) {
        var date = occurredAt.toLocalDate();
        var start = LocalDateTime.of(date, LocalTime.MIN);
        var end = LocalDateTime.of(date, LocalTime.MAX);
        doseAdministrationRepository.findByScheduleIdAndStatusAndOccurredAtBetween(
                scheduleId,
                DoseAdministrationStatus.ADMINISTERED,
                start,
                end).ifPresent(existing -> {
                    throw new DoseAlreadyAdministeredTodayException(scheduleId);
                });
    }
}
