package pe.edu.upc.medibridge.appointments.application.internal.commandservices;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl.ExternalProfilesContextService;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleFamilyVisitCommand;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleMedicalAppointmentCommand;
import pe.edu.upc.medibridge.appointments.domain.model.events.AppointmentScheduledEvent;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.InvalidAppointmentRequestException;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.InvalidAppointmentTimeSlotException;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.ProfileRelationshipNotAllowedException;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.TimeSlotNotAvailableException;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.AppointmentStatus;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.TimeSlot;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentCommandService;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.publishers.AppointmentIntegrationEventPublisher;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.AppointmentRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentCommandServiceImpl implements AppointmentCommandService {

    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    private static final int SLOT_DURATION_MINUTES = 60;
    private static final List<AppointmentStatus> ACTIVE_STATUSES =
            List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

    private final AppointmentRepository appointmentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ExternalProfilesContextService externalProfilesContextService;
    private final AppointmentIntegrationEventPublisher integrationEventPublisher;

    public AppointmentCommandServiceImpl(
            AppointmentRepository appointmentRepository,
            ApplicationEventPublisher eventPublisher,
            ExternalProfilesContextService externalProfilesContextService,
            AppointmentIntegrationEventPublisher integrationEventPublisher) {
        this.appointmentRepository = appointmentRepository;
        this.eventPublisher = eventPublisher;
        this.externalProfilesContextService = externalProfilesContextService;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<Appointment> handle(ScheduleFamilyVisitCommand command) {
        validateFamilyVisitRequiredFields(command);

        var timeSlot = buildAndValidateTimeSlot(command.startsAt(), command.durationInMinutes());
        validatePatientReference(command.patientId());
        validateFamilyMemberAccess(command.familyMemberProfileId(), command.patientId());
        validateAvailability(command.patientId(), timeSlot);

        var appointment = new Appointment(command, timeSlot);
        var savedAppointment = appointmentRepository.save(appointment);
        publishAppointmentScheduledEvent(savedAppointment);

        return Optional.of(savedAppointment);
    }

    @Override
    public Optional<Appointment> handle(ScheduleMedicalAppointmentCommand command) {
        validateMedicalAppointmentRequiredFields(command);

        var timeSlot = buildAndValidateTimeSlot(command.startsAt(), command.durationInMinutes());
        validatePatientReference(command.patientId());
        validateDoctorAssignment(command.doctorProfileId(), command.patientId());
        validateAvailability(command.patientId(), timeSlot);

        var appointment = new Appointment(command, timeSlot);
        var savedAppointment = appointmentRepository.save(appointment);
        publishAppointmentScheduledEvent(savedAppointment);

        return Optional.of(savedAppointment);
    }

    private void validateFamilyVisitRequiredFields(ScheduleFamilyVisitCommand command) {
        if (command.patientId() == null || command.patientId() <= 0) {
            throw new InvalidAppointmentRequestException("Patient id is required");
        }
        if (command.familyMemberProfileId() == null || command.familyMemberProfileId() <= 0) {
            throw new InvalidAppointmentRequestException("Family member profile id is required");
        }
        if (command.startsAt() == null) {
            throw new InvalidAppointmentRequestException("Appointment start date is required");
        }
        if (command.durationInMinutes() == null || command.durationInMinutes() <= 0) {
            throw new InvalidAppointmentRequestException("Appointment duration is required");
        }
    }

    private void validateMedicalAppointmentRequiredFields(ScheduleMedicalAppointmentCommand command) {
        if (command.patientId() == null || command.patientId() <= 0) {
            throw new InvalidAppointmentRequestException("Patient id is required");
        }
        if (command.doctorProfileId() == null || command.doctorProfileId() <= 0) {
            throw new InvalidAppointmentRequestException("Doctor profile id is required");
        }
        if (command.startsAt() == null) {
            throw new InvalidAppointmentRequestException("Appointment start date is required");
        }
        if (command.durationInMinutes() == null || command.durationInMinutes() <= 0) {
            throw new InvalidAppointmentRequestException("Appointment duration is required");
        }
    }

    private TimeSlot buildAndValidateTimeSlot(LocalDateTime startsAt, Integer durationInMinutes) {
        if (durationInMinutes != SLOT_DURATION_MINUTES) {
            throw new InvalidAppointmentTimeSlotException("Appointments must use 60-minute slots for now");
        }
        if (!startsAt.isAfter(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeSlotException("Appointments must be scheduled in the future");
        }

        var endsAt = startsAt.plusMinutes(durationInMinutes);
        var timeSlot = new TimeSlot(startsAt, endsAt);

        if (!timeSlot.getStartsAt().toLocalDate().equals(timeSlot.getEndsAt().toLocalDate())) {
            throw new InvalidAppointmentTimeSlotException("Appointments must start and end on the same day");
        }
        if (timeSlot.getStartsAt().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new InvalidAppointmentTimeSlotException("Appointments are not available on Sundays");
        }
        if (timeSlot.getStartsAt().toLocalTime().isBefore(OPENING_TIME)
                || timeSlot.getEndsAt().toLocalTime().isAfter(CLOSING_TIME)) {
            throw new InvalidAppointmentTimeSlotException("Appointments are available from 09:00 to 18:00");
        }

        return timeSlot;
    }

    private void validatePatientReference(Long patientId) {
        if (!externalProfilesContextService.patientExists(patientId)) {
            throw new InvalidPatientReferenceException(patientId);
        }
    }

    private void validateFamilyMemberAccess(Long familyMemberProfileId, Long patientId) {
        if (!externalProfilesContextService.familyMemberCanAccessPatient(familyMemberProfileId, patientId)) {
            throw new ProfileRelationshipNotAllowedException("Family member is not linked to patient");
        }
    }

    private void validateDoctorAssignment(Long doctorProfileId, Long patientId) {
        if (!externalProfilesContextService.doctorCanAttendPatient(doctorProfileId, patientId)) {
            throw new ProfileRelationshipNotAllowedException("Doctor is not assigned to patient");
        }
    }

    private void validateAvailability(Long patientId, TimeSlot timeSlot) {
        var overlaps = appointmentRepository.existsOverlappingAppointment(
                patientId,
                ACTIVE_STATUSES,
                timeSlot.getStartsAt(),
                timeSlot.getEndsAt());

        if (overlaps) {
            throw new TimeSlotNotAvailableException(patientId);
        }
    }

    private void publishAppointmentScheduledEvent(Appointment savedAppointment) {
        eventPublisher.publishEvent(new AppointmentScheduledEvent(
                savedAppointment.getId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorProfileId(),
                savedAppointment.getFamilyMemberProfileId(),
                savedAppointment.getAppointmentType().name(),
                savedAppointment.getTimeSlot().getStartsAt(),
                savedAppointment.getTimeSlot().getEndsAt()));
        integrationEventPublisher.publishAppointmentScheduled(savedAppointment);
    }
}
