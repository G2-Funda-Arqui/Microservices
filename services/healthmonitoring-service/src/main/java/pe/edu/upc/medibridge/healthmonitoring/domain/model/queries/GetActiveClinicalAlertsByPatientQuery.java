package pe.edu.upc.medibridge.healthmonitoring.domain.model.queries;

public record GetActiveClinicalAlertsByPatientQuery(Long patientId, Long requestedByUserId) {
    public GetActiveClinicalAlertsByPatientQuery(Long patientId) {
        this(patientId, null);
    }
}
