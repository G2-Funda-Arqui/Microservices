package pe.edu.upc.medibridge.medicationmanagement.domain.model.queries;

public record GetMedicationsByPatientQuery(Long patientId, Long requestedByUserId) {
    public GetMedicationsByPatientQuery(Long patientId) {
        this(patientId, null);
    }
}
