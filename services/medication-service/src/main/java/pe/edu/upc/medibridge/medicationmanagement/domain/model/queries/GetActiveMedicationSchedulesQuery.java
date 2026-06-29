package pe.edu.upc.medibridge.medicationmanagement.domain.model.queries;

public record GetActiveMedicationSchedulesQuery(Long patientId, Long requestedByUserId) {
}
