package pe.edu.upc.medibridge.appointments.domain.model.queries;

public record GetAppointmentsByPatientQuery(Long patientId, Long requestedByUserId) {
    public GetAppointmentsByPatientQuery(Long patientId) {
        this(patientId, null);
    }
}
