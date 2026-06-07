package pe.edu.upc.medibridge.healthmonitoring.application.internal.commandservices;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.healthmonitoring.application.internal.outboundservices.acl.ExternalProfilesContextService;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.ClinicalAlert;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.PatientHealthObservation;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.commands.RecordPatientHealthObservationCommand;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.events.ClinicalAlertTriggeredEvent;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.events.PatientHealthObservationRecordedEvent;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.DoctorNotAssignedToPatientException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.InvalidHealthObservationException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.AlertSeverity;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.EmotionalState;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.HealthObservationCommandService;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.messaging.publishers.HealthMonitoringIntegrationEventPublisher;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.ClinicalAlertRepository;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.PatientHealthObservationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class HealthObservationCommandServiceImpl implements HealthObservationCommandService {

    private static final BigDecimal MIN_TEMPERATURE = new BigDecimal("30.0");
    private static final BigDecimal MAX_TEMPERATURE = new BigDecimal("45.0");
    private static final BigDecimal FEVER_TEMPERATURE = new BigDecimal("38.0");

    private final PatientHealthObservationRepository patientHealthObservationRepository;
    private final ClinicalAlertRepository clinicalAlertRepository;
    private final ExternalProfilesContextService externalProfilesContextService;
    private final ApplicationEventPublisher eventPublisher;
    private final HealthMonitoringIntegrationEventPublisher integrationEventPublisher;

    public HealthObservationCommandServiceImpl(
            PatientHealthObservationRepository patientHealthObservationRepository,
            ClinicalAlertRepository clinicalAlertRepository,
            ExternalProfilesContextService externalProfilesContextService,
            ApplicationEventPublisher eventPublisher,
            HealthMonitoringIntegrationEventPublisher integrationEventPublisher) {
        this.patientHealthObservationRepository = patientHealthObservationRepository;
        this.clinicalAlertRepository = clinicalAlertRepository;
        this.externalProfilesContextService = externalProfilesContextService;
        this.eventPublisher = eventPublisher;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<PatientHealthObservation> handle(RecordPatientHealthObservationCommand command) {
        validateCommand(command);
        validateProfileReferences(command.patientId(), command.recordedByDoctorProfileId());

        var observation = patientHealthObservationRepository.save(new PatientHealthObservation(command));
        eventPublisher.publishEvent(new PatientHealthObservationRecordedEvent(
                observation.getId(),
                observation.getPatientId(),
                observation.getRecordedByDoctorProfileId(),
                observation.getRecordedAt()));
        integrationEventPublisher.publishObservationRecorded(observation);

        evaluateAndPublishClinicalAlert(observation);
        return Optional.of(observation);
    }

    private void validateCommand(RecordPatientHealthObservationCommand command) {
        validatePositiveId(command.patientId(), "Patient id is required");
        validatePositiveId(command.recordedByDoctorProfileId(), "Doctor profile id is required");
        validateBloodPressure(command.systolicBloodPressure(), command.diastolicBloodPressure());
        validateTemperature(command.bodyTemperature());
        validatePainLevel(command.painLevel());
        if (command.emotionalState() == null) {
            throw new InvalidHealthObservationException("Emotional state is required");
        }
        if (command.recordedAt() == null) {
            throw new InvalidHealthObservationException("Observation record date is required");
        }
        if (command.recordedAt().isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new InvalidHealthObservationException("Observation record date cannot be in the future");
        }
    }

    private void validatePositiveId(Long id, String message) {
        if (id == null || id <= 0) {
            throw new InvalidHealthObservationException(message);
        }
    }

    private void validateBloodPressure(Integer systolic, Integer diastolic) {
        if (systolic == null || diastolic == null) {
            throw new InvalidHealthObservationException("Blood pressure values are required");
        }
        if (systolic < 70 || systolic > 250) {
            throw new InvalidHealthObservationException("Systolic blood pressure is outside supported range");
        }
        if (diastolic < 40 || diastolic > 150) {
            throw new InvalidHealthObservationException("Diastolic blood pressure is outside supported range");
        }
        if (systolic <= diastolic) {
            throw new InvalidHealthObservationException("Systolic blood pressure must be greater than diastolic");
        }
    }

    private void validateTemperature(BigDecimal bodyTemperature) {
        if (bodyTemperature == null) {
            throw new InvalidHealthObservationException("Body temperature is required");
        }
        if (bodyTemperature.compareTo(MIN_TEMPERATURE) < 0 || bodyTemperature.compareTo(MAX_TEMPERATURE) > 0) {
            throw new InvalidHealthObservationException("Body temperature is outside supported range");
        }
    }

    private void validatePainLevel(Integer painLevel) {
        if (painLevel == null) {
            throw new InvalidHealthObservationException("Pain level is required");
        }
        if (painLevel < 0 || painLevel > 10) {
            throw new InvalidHealthObservationException("Pain level must be between 0 and 10");
        }
    }

    private void validateProfileReferences(Long patientId, Long doctorProfileId) {
        if (!externalProfilesContextService.patientExists(patientId)) {
            throw new InvalidPatientReferenceException(patientId);
        }
        if (!externalProfilesContextService.doctorCanAttendPatient(doctorProfileId, patientId)) {
            throw new DoctorNotAssignedToPatientException(doctorProfileId, patientId);
        }
    }

    private void evaluateAndPublishClinicalAlert(PatientHealthObservation observation) {
        var messages = new ArrayList<String>();
        var severity = AlertSeverity.MEDIUM;

        if (observation.getSystolicBloodPressure() >= 180 || observation.getDiastolicBloodPressure() >= 120) {
            messages.add("High blood pressure detected");
            severity = AlertSeverity.HIGH;
        }
        if (observation.getBodyTemperature().compareTo(FEVER_TEMPERATURE) >= 0) {
            messages.add("Fever-range body temperature detected");
        }
        if (observation.getPainLevel() >= 8) {
            messages.add("High pain level reported");
        }
        if (observation.getEmotionalState() == EmotionalState.CONFUSED) {
            messages.add("Confused emotional state observed");
        }

        if (messages.isEmpty()) {
            return;
        }

        var alert = clinicalAlertRepository.save(new ClinicalAlert(
                observation.getPatientId(),
                observation.getId(),
                severity,
                String.join(". ", messages) + ".",
                observation.getRecordedAt()));
        eventPublisher.publishEvent(new ClinicalAlertTriggeredEvent(
                alert.getId(),
                alert.getPatientId(),
                alert.getObservationId(),
                alert.getSeverity().name(),
                alert.getMessage()));
        if (alert.getSeverity() == AlertSeverity.HIGH) {
            integrationEventPublisher.publishClinicalAlertTriggered(alert);
        }
    }
}
