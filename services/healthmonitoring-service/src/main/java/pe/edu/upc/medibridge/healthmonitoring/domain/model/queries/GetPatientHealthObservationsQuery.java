package pe.edu.upc.medibridge.healthmonitoring.domain.model.queries;

public record GetPatientHealthObservationsQuery(Long patientId, Long requestedByUserId) {
    public GetPatientHealthObservationsQuery(Long patientId) {
        this(patientId, null);
    }
}
