package pe.edu.upc.medibridge.appointments.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentByIdQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientInPeriodQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentQueryService;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.AppointmentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentQueryServiceImpl implements AppointmentQueryService {

    private final AppointmentRepository appointmentRepository;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public AppointmentQueryServiceImpl(
            AppointmentRepository appointmentRepository,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.appointmentRepository = appointmentRepository;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    @Override
    public Optional<Appointment> handle(GetAppointmentByIdQuery query) {
        var appointment = appointmentRepository.findById(query.appointmentId());
        appointment.ifPresent(value -> authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), value.getPatientId()));
        return appointment;
    }

    @Override
    public List<Appointment> handle(GetAppointmentsByPatientQuery query) {
        if (query.requestedByUserId() != null) {
            authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), query.patientId());
        }
        return appointmentRepository.findByPatientIdOrderByStartsAtAsc(query.patientId());
    }

    @Override
    public List<Appointment> handle(GetAppointmentsByPatientInPeriodQuery query) {
        return appointmentRepository.findByPatientIdAndStartsAtBetweenOrderByStartsAtAsc(
                query.patientId(),
                query.startDate().atStartOfDay(),
                query.endDate().plusDays(1).atStartOfDay());
    }
}
