package pe.edu.upc.medibridge.appointments.domain.model.queries;

import java.time.LocalDate;

public record GetAppointmentsByPatientInPeriodQuery(
        Long patientId,
        LocalDate startDate,
        LocalDate endDate) {
}
