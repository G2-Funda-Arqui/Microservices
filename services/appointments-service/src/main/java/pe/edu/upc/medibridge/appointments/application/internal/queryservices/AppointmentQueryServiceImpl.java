package pe.edu.upc.medibridge.appointments.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentByIdQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentQueryService;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.AppointmentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentQueryServiceImpl implements AppointmentQueryService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentQueryServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Optional<Appointment> handle(GetAppointmentByIdQuery query) {
        return appointmentRepository.findById(query.appointmentId());
    }

    @Override
    public List<Appointment> handle(GetAppointmentsByPatientQuery query) {
        return appointmentRepository.findByPatientIdOrderByStartsAtAsc(query.patientId());
    }
}
