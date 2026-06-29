package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetActiveClinicalAlertsByPatientQuery;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetPatientHealthObservationsQuery;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.ClinicalAlertQueryService;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.HealthObservationQueryService;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.AlertStatus;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.ClinicalAlertRepository;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.PatientHealthObservationRepository;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class HealthMonitoringContextFacade {

    private final HealthObservationQueryService healthObservationQueryService;
    private final ClinicalAlertQueryService clinicalAlertQueryService;
    private final PatientHealthObservationRepository patientHealthObservationRepository;
    private final ClinicalAlertRepository clinicalAlertRepository;

    public HealthMonitoringContextFacade(
            HealthObservationQueryService healthObservationQueryService,
            ClinicalAlertQueryService clinicalAlertQueryService,
            PatientHealthObservationRepository patientHealthObservationRepository,
            ClinicalAlertRepository clinicalAlertRepository) {
        this.healthObservationQueryService = healthObservationQueryService;
        this.clinicalAlertQueryService = clinicalAlertQueryService;
        this.patientHealthObservationRepository = patientHealthObservationRepository;
        this.clinicalAlertRepository = clinicalAlertRepository;
    }

    public String fetchPatientClinicalSummaryByPatientId(Long patientId) {
        var observations = healthObservationQueryService.handle(new GetPatientHealthObservationsQuery(patientId));
        if (observations.isEmpty()) {
            return "No health monitoring observations registered for this patient.";
        }

        var latest = observations.getFirst();
        var observationSummary = "Latest health observation recorded at " + latest.getRecordedAt()
                + ": blood pressure " + latest.getSystolicBloodPressure() + "/"
                + latest.getDiastolicBloodPressure() + " mmHg"
                + ", body temperature " + latest.getBodyTemperature().stripTrailingZeros().toPlainString() + " C"
                + ", pain level " + latest.getPainLevel() + "/10"
                + ", emotional state " + latest.getEmotionalState() + ".";

        var recentSummary = observations.stream()
                .limit(5)
                .map(observation -> observation.getRecordedAt()
                        + " BP " + observation.getSystolicBloodPressure()
                        + "/" + observation.getDiastolicBloodPressure()
                        + ", temp " + observation.getBodyTemperature().stripTrailingZeros().toPlainString()
                        + " C, pain " + observation.getPainLevel()
                        + ", mood " + observation.getEmotionalState())
                .collect(Collectors.joining("; "));

        var alerts = clinicalAlertQueryService.handle(new GetActiveClinicalAlertsByPatientQuery(patientId));
        var alertSummary = alerts.isEmpty()
                ? "No active clinical alerts."
                : "Active clinical alerts: " + alerts.stream()
                .map(alert -> alert.getSeverity() + " - " + alert.getMessage())
                .collect(Collectors.joining(" "));

        return observationSummary + " Recent observations: " + recentSummary + ". " + alertSummary;
    }

    public String fetchPatientClinicalSummaryByPatientIdAndPeriod(Long patientId, LocalDate startDate, LocalDate endDate) {
        var start = startDate.atStartOfDay();
        var end = endDate.plusDays(1).atStartOfDay();
        var observations = patientHealthObservationRepository.findByPatientIdAndRecordedAtBetweenOrderByRecordedAtDesc(
                patientId,
                start,
                end);
        if (observations.isEmpty()) {
            return "No health monitoring observations registered for this patient in the report period.";
        }

        var latest = observations.getFirst();
        var observationSummary = "Latest health observation in report period recorded at " + latest.getRecordedAt()
                + ": blood pressure " + latest.getSystolicBloodPressure() + "/"
                + latest.getDiastolicBloodPressure() + " mmHg"
                + ", body temperature " + latest.getBodyTemperature().stripTrailingZeros().toPlainString() + " C"
                + ", pain level " + latest.getPainLevel() + "/10"
                + ", emotional state " + latest.getEmotionalState() + ".";

        var recentSummary = observations.stream()
                .limit(5)
                .map(observation -> observation.getRecordedAt()
                        + " BP " + observation.getSystolicBloodPressure()
                        + "/" + observation.getDiastolicBloodPressure()
                        + ", temp " + observation.getBodyTemperature().stripTrailingZeros().toPlainString()
                        + " C, pain " + observation.getPainLevel()
                        + ", mood " + observation.getEmotionalState())
                .collect(Collectors.joining("; "));

        var alerts = clinicalAlertRepository.findByPatientIdAndStatusAndTriggeredAtBetweenOrderByTriggeredAtDesc(
                patientId,
                AlertStatus.ACTIVE,
                start,
                end);
        var alertSummary = alerts.isEmpty()
                ? "No active clinical alerts in the report period."
                : "Active clinical alerts in report period: " + alerts.stream()
                .map(alert -> alert.getSeverity() + " - " + alert.getMessage())
                .collect(Collectors.joining(" "));

        return observationSummary + " Recent observations in report period: " + recentSummary + ". " + alertSummary;
    }
}
