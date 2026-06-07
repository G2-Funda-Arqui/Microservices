package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetActiveClinicalAlertsByPatientQuery;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetPatientHealthObservationsQuery;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.ClinicalAlertQueryService;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.HealthObservationQueryService;

import java.util.stream.Collectors;

@Service
public class HealthMonitoringContextFacade {

    private final HealthObservationQueryService healthObservationQueryService;
    private final ClinicalAlertQueryService clinicalAlertQueryService;

    public HealthMonitoringContextFacade(
            HealthObservationQueryService healthObservationQueryService,
            ClinicalAlertQueryService clinicalAlertQueryService) {
        this.healthObservationQueryService = healthObservationQueryService;
        this.clinicalAlertQueryService = clinicalAlertQueryService;
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
}
