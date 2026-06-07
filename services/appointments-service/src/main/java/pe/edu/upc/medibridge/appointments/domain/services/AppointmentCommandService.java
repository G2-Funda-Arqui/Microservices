package pe.edu.upc.medibridge.appointments.domain.services;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleFamilyVisitCommand;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleMedicalAppointmentCommand;

import java.util.Optional;

public interface AppointmentCommandService {
    Optional<Appointment> handle(ScheduleFamilyVisitCommand command);
    Optional<Appointment> handle(ScheduleMedicalAppointmentCommand command);
}
