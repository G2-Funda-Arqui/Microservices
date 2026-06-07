package pe.edu.upc.medibridge.appointments.domain.services;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentByIdQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;

import java.util.List;
import java.util.Optional;

public interface AppointmentQueryService {
    Optional<Appointment> handle(GetAppointmentByIdQuery query);
    List<Appointment> handle(GetAppointmentsByPatientQuery query);
}
