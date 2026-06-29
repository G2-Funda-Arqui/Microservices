# appointments-service

> Export generado desde el repositorio medibridge. Se excluye target/ porque contiene artefactos compilados.

## Ubicacion en el repositorio

```text
medibridge/
  services/
    appointments-service/
```

Ruta relativa: `services/appointments-service`

## Estructura del servicio

```text
appointments-service/
Dockerfile
pom.xml
src/
  main/
    java/
      pe/
        edu/
          upc/
            medibridge/
              appointments/
                application/
                  internal/
                    commandservices/
                      AppointmentCommandServiceImpl.java
                    eventhandlers/
                      DoctorAssignedToPatientEventHandler.java
                      FamilyMemberAssignedToPatientEventHandler.java
                      PatientDeactivatedEventHandler.java
                      PatientRegisteredEventHandler.java
                    outboundservices/
                      acl/
                        ExternalProfilesContextService.java
                    queryservices/
                      AppointmentQueryServiceImpl.java
                AppointmentsServiceApplication.java
                domain/
                  model/
                    aggregates/
                      Appointment.java
                    commands/
                      ScheduleFamilyVisitCommand.java
                      ScheduleMedicalAppointmentCommand.java
                    entities/
                      DoctorPatientRelation.java
                      FamilyPatientRelation.java
                      PatientReference.java
                    events/
                      AppointmentScheduledEvent.java
                    exceptions/
                      InvalidAppointmentRequestException.java
                      InvalidAppointmentTimeSlotException.java
                      InvalidPatientReferenceException.java
                      ProfileRelationshipNotAllowedException.java
                      TimeSlotNotAvailableException.java
                    queries/
                      GetAppointmentByIdQuery.java
                      GetAppointmentsByPatientInPeriodQuery.java
                      GetAppointmentsByPatientQuery.java
                    valueobjects/
                      AppointmentStatus.java
                      AppointmentType.java
                      TimeSlot.java
                  services/
                    AppointmentCommandService.java
                    AppointmentQueryService.java
                infrastructure/
                  messaging/
                    events/
                      AppointmentScheduledIntegrationEvent.java
                      DoctorAssignedToPatientIntegrationEvent.java
                      FamilyMemberAssignedToPatientIntegrationEvent.java
                      PatientDeactivatedIntegrationEvent.java
                      PatientRegisteredIntegrationEvent.java
                    publishers/
                      AppointmentIntegrationEventPublisher.java
                    RabbitMQConfiguration.java
                  persistence/
                    jpa/
                      repositories/
                        AppointmentRepository.java
                        DoctorPatientRelationRepository.java
                        FamilyPatientRelationRepository.java
                        PatientReferenceRepository.java
                  security/
                    SecurityConfiguration.java
                interfaces/
                  rest/
                    acl/
                      AppointmentContextFacade.java
                      ProfilesContextFacade.java
                    controllers/
                      AppointmentsController.java
                      AppointmentsInternalController.java
                    resources/
                      AppointmentResource.java
                      FamilyVisitResource.java
                      MedicalAppointmentResource.java
                      ScheduleFamilyVisitResource.java
                      ScheduleMedicalAppointmentResource.java
                    transform/
                      AppointmentResourceFromEntityAssembler.java
                      FamilyVisitResourceFromEntityAssembler.java
                      MedicalAppointmentResourceFromEntityAssembler.java
                      ScheduleFamilyVisitCommandFromResourceAssembler.java
                      ScheduleMedicalAppointmentCommandFromResourceAssembler.java
              shared/
                domain/
                  model/
                    aggregates/
                      AuditableAbstractAggregateRoot.java
                infrastructure/
                  documentation/
                    openapi/
                      configuration/
                        OpenApiConfiguration.java
                interfaces/
                  rest/
                    exception/
                      GlobalExceptionHandler.java
                    resources/
                      ErrorResponseResource.java
                      MessageResource.java
    resources/
      application.yml
      db/
        migration/
          V1__appointments_schema.sql
          V2__local_references.sql
```

## Codigo fuente

### `services/appointments-service/Dockerfile`

~~~dockerfile
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /workspace/target/*-SNAPSHOT.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
~~~

### `services/appointments-service/pom.xml`

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/>
    </parent>

    <groupId>pe.edu.upc.medibridge</groupId>
    <artifactId>appointments-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>appointments-service</name>
    <description>MediBridge Appointments microservice</description>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2025.0.0</spring-cloud.version>
        <springdoc.version>2.8.8</springdoc.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/application/internal/commandservices/AppointmentCommandServiceImpl.java`

~~~java
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
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/application/internal/eventhandlers/DoctorAssignedToPatientEventHandler.java`

~~~java
package pe.edu.upc.medibridge.appointments.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.domain.model.entities.DoctorPatientRelation;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.DoctorAssignedToPatientIntegrationEvent;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.DoctorPatientRelationRepository;

@Component
public class DoctorAssignedToPatientEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorAssignedToPatientEventHandler.class);
    private final DoctorPatientRelationRepository doctorPatientRelationRepository;

    public DoctorAssignedToPatientEventHandler(DoctorPatientRelationRepository doctorPatientRelationRepository) {
        this.doctorPatientRelationRepository = doctorPatientRelationRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_DOCTOR_ASSIGNED_PATIENT)
    public void on(DoctorAssignedToPatientIntegrationEvent event) {
        if (!doctorPatientRelationRepository.existsByAssignmentId(event.assignmentId())) {
            doctorPatientRelationRepository.save(new DoctorPatientRelation(
                    event.assignmentId(),
                    event.doctorProfileId(),
                    event.patientId()));
        }
        LOGGER.info("Doctor-patient relation received by appointments: doctorProfileId={}, patientId={}",
                event.doctorProfileId(), event.patientId());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/application/internal/eventhandlers/FamilyMemberAssignedToPatientEventHandler.java`

~~~java
package pe.edu.upc.medibridge.appointments.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.domain.model.entities.FamilyPatientRelation;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.FamilyMemberAssignedToPatientIntegrationEvent;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.FamilyPatientRelationRepository;

@Component
public class FamilyMemberAssignedToPatientEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyMemberAssignedToPatientEventHandler.class);
    private final FamilyPatientRelationRepository familyPatientRelationRepository;

    public FamilyMemberAssignedToPatientEventHandler(FamilyPatientRelationRepository familyPatientRelationRepository) {
        this.familyPatientRelationRepository = familyPatientRelationRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_FAMILY_ASSIGNED_PATIENT)
    public void on(FamilyMemberAssignedToPatientIntegrationEvent event) {
        if (!familyPatientRelationRepository.existsByLinkId(event.linkId())) {
            familyPatientRelationRepository.save(new FamilyPatientRelation(
                    event.linkId(),
                    event.familyMemberProfileId(),
                    event.patientId()));
        }
        LOGGER.info("Family-patient relation received by appointments: familyMemberProfileId={}, patientId={}",
                event.familyMemberProfileId(), event.patientId());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/application/internal/eventhandlers/PatientDeactivatedEventHandler.java`

~~~java
package pe.edu.upc.medibridge.appointments.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.PatientDeactivatedIntegrationEvent;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Component
public class PatientDeactivatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDeactivatedEventHandler.class);
    private final PatientReferenceRepository patientReferenceRepository;

    public PatientDeactivatedEventHandler(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PATIENT_DEACTIVATED)
    public void on(PatientDeactivatedIntegrationEvent event) {
        patientReferenceRepository.findByPatientId(event.patientId()).ifPresent(patient -> {
            patient.deactivate();
            patientReferenceRepository.save(patient);
        });
        LOGGER.info("Patient deactivation received by appointments: patientId={}", event.patientId());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/application/internal/eventhandlers/PatientRegisteredEventHandler.java`

~~~java
package pe.edu.upc.medibridge.appointments.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.domain.model.entities.PatientReference;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.PatientRegisteredIntegrationEvent;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Component
public class PatientRegisteredEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRegisteredEventHandler.class);
    private final PatientReferenceRepository patientReferenceRepository;

    public PatientRegisteredEventHandler(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PATIENT_REGISTERED)
    public void on(PatientRegisteredIntegrationEvent event) {
        if (patientReferenceRepository.findByPatientId(event.patientId()).isEmpty()) {
            patientReferenceRepository.save(new PatientReference(event.patientId(), event.fullName()));
        }
        LOGGER.info("Patient reference received by appointments: patientId={}, fullName={}",
                event.patientId(), event.fullName());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/application/internal/outboundservices/acl/ExternalProfilesContextService.java`

~~~java
package pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl;

public interface ExternalProfilesContextService {
    boolean patientExists(Long patientId);
    boolean familyMemberCanAccessPatient(Long familyMemberProfileId, Long patientId);
    boolean doctorCanAttendPatient(Long doctorProfileId, Long patientId);
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/application/internal/queryservices/AppointmentQueryServiceImpl.java`

~~~java
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

    @Override
    public List<Appointment> handle(GetAppointmentsByPatientInPeriodQuery query) {
        return appointmentRepository.findByPatientIdAndStartsAtBetweenOrderByStartsAtAsc(
                query.patientId(),
                query.startDate().atStartOfDay(),
                query.endDate().plusDays(1).atStartOfDay());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/AppointmentsServiceApplication.java`

~~~java
package pe.edu.upc.medibridge.appointments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients(basePackages = "pe.edu.upc.medibridge.appointments.infrastructure.acl")
@SpringBootApplication(scanBasePackages = "pe.edu.upc.medibridge")
public class AppointmentsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppointmentsServiceApplication.class, args);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/aggregates/Appointment.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.aggregates;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleFamilyVisitCommand;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleMedicalAppointmentCommand;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.AppointmentStatus;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.AppointmentType;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.TimeSlot;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "appointments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Appointment extends AuditableAbstractAggregateRoot<Appointment> {

    @NotNull
    @Column(nullable = false)
    private Long patientId;

    private Long familyMemberProfileId;

    private Long doctorProfileId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentType appointmentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startsAt", column = @Column(name = "starts_at", nullable = false)),
            @AttributeOverride(name = "endsAt", column = @Column(name = "ends_at", nullable = false))
    })
    private TimeSlot timeSlot;

    @Size(max = 240)
    @Column(length = 240)
    private String reason;

    public Appointment(ScheduleFamilyVisitCommand command, TimeSlot timeSlot) {
        this.patientId = command.patientId();
        this.familyMemberProfileId = command.familyMemberProfileId();
        this.appointmentType = AppointmentType.FAMILY_VISIT;
        this.status = AppointmentStatus.SCHEDULED;
        this.timeSlot = timeSlot;
        this.reason = command.reason();
    }

    public Appointment(ScheduleMedicalAppointmentCommand command, TimeSlot timeSlot) {
        this.patientId = command.patientId();
        this.doctorProfileId = command.doctorProfileId();
        this.appointmentType = AppointmentType.MEDICAL;
        this.status = AppointmentStatus.SCHEDULED;
        this.timeSlot = timeSlot;
        this.reason = command.reason();
    }

    public boolean hasActiveSchedule() {
        return status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.CONFIRMED;
    }

    public boolean overlaps(TimeSlot candidate) {
        return hasActiveSchedule() && timeSlot.overlaps(candidate);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/commands/ScheduleFamilyVisitCommand.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.commands;

import java.time.LocalDateTime;

public record ScheduleFamilyVisitCommand(
        Long patientId,
        Long familyMemberProfileId,
        LocalDateTime startsAt,
        Integer durationInMinutes,
        String reason) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/commands/ScheduleMedicalAppointmentCommand.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.commands;

import java.time.LocalDateTime;

public record ScheduleMedicalAppointmentCommand(
        Long patientId,
        Long doctorProfileId,
        LocalDateTime startsAt,
        Integer durationInMinutes,
        String reason) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/entities/DoctorPatientRelation.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "doctor_patient_relations")
@NoArgsConstructor
public class DoctorPatientRelation extends AuditableAbstractAggregateRoot<DoctorPatientRelation> {
    @Column(nullable = false)
    private Long assignmentId;

    @Column(nullable = false)
    private Long doctorProfileId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private boolean active;

    public DoctorPatientRelation(Long assignmentId, Long doctorProfileId, Long patientId) {
        this.assignmentId = assignmentId;
        this.doctorProfileId = doctorProfileId;
        this.patientId = patientId;
        this.active = true;
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/entities/FamilyPatientRelation.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "family_patient_relations")
@NoArgsConstructor
public class FamilyPatientRelation extends AuditableAbstractAggregateRoot<FamilyPatientRelation> {
    @Column(nullable = false)
    private Long linkId;

    @Column(nullable = false)
    private Long familyMemberProfileId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private boolean active;

    public FamilyPatientRelation(Long linkId, Long familyMemberProfileId, Long patientId) {
        this.linkId = linkId;
        this.familyMemberProfileId = familyMemberProfileId;
        this.patientId = patientId;
        this.active = true;
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/entities/PatientReference.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@Table(name = "patient_references")
@NoArgsConstructor
public class PatientReference extends AuditableAbstractAggregateRoot<PatientReference> {
    @Column(nullable = false, unique = true)
    private Long patientId;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false)
    private boolean active;

    public PatientReference(Long patientId, String fullName) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/events/AppointmentScheduledEvent.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.events;

import java.time.Instant;
import java.time.LocalDateTime;

public record AppointmentScheduledEvent(
        Long appointmentId,
        Long patientId,
        Long doctorProfileId,
        Long familyMemberProfileId,
        String appointmentType,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Instant occurredAt,
        int version) {

    public AppointmentScheduledEvent(
            Long appointmentId,
            Long patientId,
            Long doctorProfileId,
            Long familyMemberProfileId,
            String appointmentType,
            LocalDateTime startsAt,
            LocalDateTime endsAt) {
        this(appointmentId, patientId, doctorProfileId, familyMemberProfileId, appointmentType, startsAt, endsAt, Instant.now(), 1);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/exceptions/InvalidAppointmentRequestException.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAppointmentRequestException extends RuntimeException {
    public InvalidAppointmentRequestException(String message) {
        super(message);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/exceptions/InvalidAppointmentTimeSlotException.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAppointmentTimeSlotException extends RuntimeException {
    public InvalidAppointmentTimeSlotException(String message) {
        super(message);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/exceptions/InvalidPatientReferenceException.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidPatientReferenceException extends RuntimeException {
    public InvalidPatientReferenceException(Long patientId) {
        super("Patient reference not found: " + patientId);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/exceptions/ProfileRelationshipNotAllowedException.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ProfileRelationshipNotAllowedException extends RuntimeException {
    public ProfileRelationshipNotAllowedException(String message) {
        super(message);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/exceptions/TimeSlotNotAvailableException.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TimeSlotNotAvailableException extends RuntimeException {
    public TimeSlotNotAvailableException(Long patientId) {
        super("Time slot is not available for patient: " + patientId);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/queries/GetAppointmentByIdQuery.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.queries;

public record GetAppointmentByIdQuery(Long appointmentId) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/queries/GetAppointmentsByPatientInPeriodQuery.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.queries;

import java.time.LocalDate;

public record GetAppointmentsByPatientInPeriodQuery(
        Long patientId,
        LocalDate startDate,
        LocalDate endDate) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/queries/GetAppointmentsByPatientQuery.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.queries;

public record GetAppointmentsByPatientQuery(Long patientId) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/valueobjects/AppointmentStatus.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.valueobjects;

public enum AppointmentStatus {
    SCHEDULED,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/valueobjects/AppointmentType.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.valueobjects;

public enum AppointmentType {
    MEDICAL,
    FAMILY_VISIT
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/model/valueobjects/TimeSlot.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeSlot {

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    public TimeSlot(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (startsAt == null || endsAt == null) {
            throw new IllegalArgumentException("Time slot start and end are required");
        }
        if (!startsAt.isBefore(endsAt)) {
            throw new IllegalArgumentException("Time slot start must be before end");
        }
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public boolean overlaps(TimeSlot other) {
        return startsAt.isBefore(other.endsAt) && endsAt.isAfter(other.startsAt);
    }

    public long durationInMinutes() {
        return Duration.between(startsAt, endsAt).toMinutes();
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/services/AppointmentCommandService.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.services;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleFamilyVisitCommand;
import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleMedicalAppointmentCommand;

import java.util.Optional;

public interface AppointmentCommandService {
    Optional<Appointment> handle(ScheduleFamilyVisitCommand command);
    Optional<Appointment> handle(ScheduleMedicalAppointmentCommand command);
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/domain/services/AppointmentQueryService.java`

~~~java
package pe.edu.upc.medibridge.appointments.domain.services;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentByIdQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientInPeriodQuery;

import java.util.List;
import java.util.Optional;

public interface AppointmentQueryService {
    Optional<Appointment> handle(GetAppointmentByIdQuery query);
    List<Appointment> handle(GetAppointmentsByPatientQuery query);
    List<Appointment> handle(GetAppointmentsByPatientInPeriodQuery query);
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/messaging/events/AppointmentScheduledIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.messaging.events;

import java.time.Instant;
import java.time.LocalDateTime;

public record AppointmentScheduledIntegrationEvent(
        Long appointmentId,
        Long patientId,
        Long doctorProfileId,
        Long familyMemberProfileId,
        String appointmentType,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Instant occurredAt,
        int version
) {
    public AppointmentScheduledIntegrationEvent(
            Long appointmentId,
            Long patientId,
            Long doctorProfileId,
            Long familyMemberProfileId,
            String appointmentType,
            LocalDateTime startsAt,
            LocalDateTime endsAt) {
        this(appointmentId, patientId, doctorProfileId, familyMemberProfileId, appointmentType, startsAt, endsAt, Instant.now(), 1);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/messaging/events/DoctorAssignedToPatientIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.messaging.events;

import java.time.Instant;

public record DoctorAssignedToPatientIntegrationEvent(
        Long assignmentId,
        Long doctorProfileId,
        Long patientId,
        Instant occurredAt,
        int version
) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/messaging/events/FamilyMemberAssignedToPatientIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.messaging.events;

import java.time.Instant;

public record FamilyMemberAssignedToPatientIntegrationEvent(
        Long linkId,
        Long familyMemberProfileId,
        Long patientId,
        Instant occurredAt,
        int version
) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/messaging/events/PatientDeactivatedIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.messaging.events;

import java.time.Instant;

public record PatientDeactivatedIntegrationEvent(
        Long patientId,
        Instant occurredAt,
        int version
) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/messaging/events/PatientRegisteredIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.messaging.events;

import java.time.Instant;

public record PatientRegisteredIntegrationEvent(
        Long patientId,
        String fullName,
        Instant occurredAt,
        int version
) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/messaging/publishers/AppointmentIntegrationEventPublisher.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.messaging.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.appointments.infrastructure.messaging.events.AppointmentScheduledIntegrationEvent;

@Component
public class AppointmentIntegrationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public AppointmentIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAppointmentScheduled(Appointment appointment) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_APPOINTMENT_SCHEDULED,
                new AppointmentScheduledIntegrationEvent(
                        appointment.getId(),
                        appointment.getPatientId(),
                        appointment.getDoctorProfileId(),
                        appointment.getFamilyMemberProfileId(),
                        appointment.getAppointmentType().name(),
                        appointment.getTimeSlot().getStartsAt(),
                        appointment.getTimeSlot().getEndsAt()));
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/messaging/RabbitMQConfiguration.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    public static final String EXCHANGE = "medibridge.events";
    public static final String DLX = "medibridge.dlx";

    public static final String ROUTING_KEY_PATIENT_REGISTERED = "patient.registered";
    public static final String ROUTING_KEY_PATIENT_DEACTIVATED = "patient.deactivated";
    public static final String ROUTING_KEY_DOCTOR_ASSIGNED_PATIENT = "doctor.assigned.patient";
    public static final String ROUTING_KEY_FAMILY_ASSIGNED_PATIENT = "family.assigned.patient";
    public static final String ROUTING_KEY_APPOINTMENT_SCHEDULED = "appointment.scheduled";

    public static final String QUEUE_PATIENT_REGISTERED = "appointments.patient-registered";
    public static final String QUEUE_PATIENT_DEACTIVATED = "appointments.patient-deactivated";
    public static final String QUEUE_DOCTOR_ASSIGNED_PATIENT = "appointments.doctor-assigned-patient";
    public static final String QUEUE_FAMILY_ASSIGNED_PATIENT = "appointments.family-assigned-patient";

    @Bean
    public TopicExchange medibridgeEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue patientRegisteredQueue() {
        return durableQueue(QUEUE_PATIENT_REGISTERED);
    }

    @Bean
    public Queue patientDeactivatedQueue() {
        return durableQueue(QUEUE_PATIENT_DEACTIVATED);
    }

    @Bean
    public Queue doctorAssignedPatientQueue() {
        return durableQueue(QUEUE_DOCTOR_ASSIGNED_PATIENT);
    }

    @Bean
    public Queue familyAssignedPatientQueue() {
        return durableQueue(QUEUE_FAMILY_ASSIGNED_PATIENT);
    }

    @Bean
    public Binding patientRegisteredBinding(Queue patientRegisteredQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(patientRegisteredQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_PATIENT_REGISTERED);
    }

    @Bean
    public Binding patientDeactivatedBinding(Queue patientDeactivatedQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(patientDeactivatedQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_PATIENT_DEACTIVATED);
    }

    @Bean
    public Binding doctorAssignedPatientBinding(Queue doctorAssignedPatientQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(doctorAssignedPatientQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_DOCTOR_ASSIGNED_PATIENT);
    }

    @Bean
    public Binding familyAssignedPatientBinding(Queue familyAssignedPatientQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(familyAssignedPatientQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_FAMILY_ASSIGNED_PATIENT);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setExchange(EXCHANGE);
        return template;
    }

    private Queue durableQueue(String name) {
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", DLX)
                .build();
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/persistence/jpa/repositories/AppointmentRepository.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.valueobjects.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            select a
            from Appointment a
            where a.patientId = :patientId
            order by a.timeSlot.startsAt asc
            """)
    List<Appointment> findByPatientIdOrderByStartsAtAsc(@Param("patientId") Long patientId);

    @Query("""
            select a
            from Appointment a
            where a.patientId = :patientId
              and a.timeSlot.startsAt >= :startsAt
              and a.timeSlot.startsAt < :endsAt
            order by a.timeSlot.startsAt asc
            """)
    List<Appointment> findByPatientIdAndStartsAtBetweenOrderByStartsAtAsc(
            @Param("patientId") Long patientId,
            @Param("startsAt") LocalDateTime startsAt,
            @Param("endsAt") LocalDateTime endsAt);

    @Query("""
            select count(a) > 0
            from Appointment a
            where a.patientId = :patientId
              and a.status in :activeStatuses
              and a.timeSlot.startsAt < :endsAt
              and a.timeSlot.endsAt > :startsAt
            """)
    boolean existsOverlappingAppointment(
            @Param("patientId") Long patientId,
            @Param("activeStatuses") Collection<AppointmentStatus> activeStatuses,
            @Param("startsAt") LocalDateTime startsAt,
            @Param("endsAt") LocalDateTime endsAt);
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/persistence/jpa/repositories/DoctorPatientRelationRepository.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.appointments.domain.model.entities.DoctorPatientRelation;

@Repository
public interface DoctorPatientRelationRepository extends JpaRepository<DoctorPatientRelation, Long> {
    boolean existsByAssignmentId(Long assignmentId);
    boolean existsByDoctorProfileIdAndPatientIdAndActiveTrue(Long doctorProfileId, Long patientId);
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/persistence/jpa/repositories/FamilyPatientRelationRepository.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.appointments.domain.model.entities.FamilyPatientRelation;

@Repository
public interface FamilyPatientRelationRepository extends JpaRepository<FamilyPatientRelation, Long> {
    boolean existsByLinkId(Long linkId);
    boolean existsByFamilyMemberProfileIdAndPatientIdAndActiveTrue(Long familyMemberProfileId, Long patientId);
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/persistence/jpa/repositories/PatientReferenceRepository.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.appointments.domain.model.entities.PatientReference;

import java.util.Optional;

@Repository
public interface PatientReferenceRepository extends JpaRepository<PatientReference, Long> {
    boolean existsByPatientIdAndActiveTrue(Long patientId);
    Optional<PatientReference> findByPatientId(Long patientId);
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/infrastructure/security/SecurityConfiguration.java`

~~~java
package pe.edu.upc.medibridge.appointments.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health",
                                "/api/v1/internal/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/acl/AppointmentContextFacade.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientInPeriodQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentQueryService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentContextFacade {
    private final AppointmentQueryService appointmentQueryService;

    public AppointmentContextFacade(AppointmentQueryService appointmentQueryService) {
        this.appointmentQueryService = appointmentQueryService;
    }

    public String fetchAppointmentSummaryByPatientId(Long patientId) {
        var appointments = appointmentQueryService.handle(new GetAppointmentsByPatientQuery(patientId));
        return summarizeAppointments(appointments, "No appointments registered for this patient.");
    }

    public String fetchAppointmentSummaryByPatientIdAndPeriod(Long patientId, LocalDate startDate, LocalDate endDate) {
        var appointments = appointmentQueryService.handle(
                new GetAppointmentsByPatientInPeriodQuery(patientId, startDate, endDate));
        return summarizeAppointments(appointments, "No appointments registered for this patient in the report period.");
    }

    private String summarizeAppointments(List<Appointment> appointments, String emptyMessage) {
        if (appointments.isEmpty()) {
            return emptyMessage;
        }
        return appointments.stream()
                .map(appointment -> {
                    var timeSlot = appointment.getTimeSlot();
                    var professional = appointment.getDoctorProfileId() != null
                            ? "assigned doctor"
                            : "family member";
                    var reason = appointment.getReason() == null || appointment.getReason().isBlank()
                            ? "No reason registered"
                            : appointment.getReason();
                    return appointment.getAppointmentType()
                            + " appointment with " + professional
                            + " from " + timeSlot.getStartsAt()
                            + " to " + timeSlot.getEndsAt()
                            + ". Status: " + appointment.getStatus()
                            + ". Reason: " + reason + ".";
                })
                .collect(Collectors.joining(" "));
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/acl/ProfilesContextFacade.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl.ExternalProfilesContextService;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.DoctorPatientRelationRepository;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.FamilyPatientRelationRepository;
import pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Service
public class ProfilesContextFacade implements ExternalProfilesContextService {

    private final PatientReferenceRepository patientReferenceRepository;
    private final DoctorPatientRelationRepository doctorPatientRelationRepository;
    private final FamilyPatientRelationRepository familyPatientRelationRepository;

    public ProfilesContextFacade(
            PatientReferenceRepository patientReferenceRepository,
            DoctorPatientRelationRepository doctorPatientRelationRepository,
            FamilyPatientRelationRepository familyPatientRelationRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
        this.doctorPatientRelationRepository = doctorPatientRelationRepository;
        this.familyPatientRelationRepository = familyPatientRelationRepository;
    }

    @Override
    public boolean patientExists(Long patientId) {
        return patientId != null && patientReferenceRepository.existsByPatientIdAndActiveTrue(patientId);
    }

    @Override
    public boolean familyMemberCanAccessPatient(Long familyMemberProfileId, Long patientId) {
        return familyMemberProfileId != null
                && patientId != null
                && familyPatientRelationRepository.existsByFamilyMemberProfileIdAndPatientIdAndActiveTrue(
                familyMemberProfileId,
                patientId);
    }

    @Override
    public boolean doctorCanAttendPatient(Long doctorProfileId, Long patientId) {
        return doctorProfileId != null
                && patientId != null
                && doctorPatientRelationRepository.existsByDoctorProfileIdAndPatientIdAndActiveTrue(
                doctorProfileId,
                patientId);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/controllers/AppointmentsController.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentByIdQuery;
import pe.edu.upc.medibridge.appointments.domain.model.queries.GetAppointmentsByPatientQuery;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentCommandService;
import pe.edu.upc.medibridge.appointments.domain.services.AppointmentQueryService;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.AppointmentResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.FamilyVisitResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.MedicalAppointmentResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.ScheduleFamilyVisitResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.ScheduleMedicalAppointmentResource;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.AppointmentResourceFromEntityAssembler;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.FamilyVisitResourceFromEntityAssembler;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.MedicalAppointmentResourceFromEntityAssembler;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.ScheduleFamilyVisitCommandFromResourceAssembler;
import pe.edu.upc.medibridge.appointments.interfaces.rest.transform.ScheduleMedicalAppointmentCommandFromResourceAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Appointments", description = "Appointment Scheduling Endpoints")
public class AppointmentsController {

    private final AppointmentCommandService appointmentCommandService;
    private final AppointmentQueryService appointmentQueryService;

    public AppointmentsController(
            AppointmentCommandService appointmentCommandService,
            AppointmentQueryService appointmentQueryService) {
        this.appointmentCommandService = appointmentCommandService;
        this.appointmentQueryService = appointmentQueryService;
    }

    @PostMapping(value = "/family-visits", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FamilyVisitResource> scheduleFamilyVisit(
            @RequestBody ScheduleFamilyVisitResource resource) {
        var command = ScheduleFamilyVisitCommandFromResourceAssembler.toCommandFromResource(resource);
        var appointment = appointmentCommandService.handle(command);

        if (appointment.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var appointmentResource = FamilyVisitResourceFromEntityAssembler.toResourceFromEntity(appointment.get());
        return new ResponseEntity<>(appointmentResource, HttpStatus.CREATED);
    }

    @PostMapping(value = "/medical", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicalAppointmentResource> scheduleMedicalAppointment(
            @RequestBody ScheduleMedicalAppointmentResource resource) {
        var command = ScheduleMedicalAppointmentCommandFromResourceAssembler.toCommandFromResource(resource);
        var appointment = appointmentCommandService.handle(command);

        if (appointment.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var appointmentResource = MedicalAppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment.get());
        return new ResponseEntity<>(appointmentResource, HttpStatus.CREATED);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResource> getAppointmentById(@PathVariable Long appointmentId) {
        var appointment = appointmentQueryService.handle(new GetAppointmentByIdQuery(appointmentId));

        if (appointment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var appointmentResource = AppointmentResourceFromEntityAssembler.toResourceFromEntity(appointment.get());
        return ResponseEntity.ok(appointmentResource);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResource>> getAppointmentsByPatient(@PathVariable Long patientId) {
        var appointments = appointmentQueryService.handle(new GetAppointmentsByPatientQuery(patientId));
        var appointmentResources = appointments.stream()
                .map(AppointmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(appointmentResources);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/controllers/AppointmentsInternalController.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.appointments.interfaces.rest.acl.AppointmentContextFacade;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/internal/appointments")
public class AppointmentsInternalController {
    private final AppointmentContextFacade appointmentContextFacade;

    public AppointmentsInternalController(AppointmentContextFacade appointmentContextFacade) {
        this.appointmentContextFacade = appointmentContextFacade;
    }

    @GetMapping("/patients/{patientId}/summary")
    public String getAppointmentSummaryByPatientId(
            @PathVariable Long patientId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return appointmentContextFacade.fetchAppointmentSummaryByPatientIdAndPeriod(patientId, startDate, endDate);
        }
        return appointmentContextFacade.fetchAppointmentSummaryByPatientId(patientId);
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/resources/AppointmentResource.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record AppointmentResource(
        Long id,
        Long patientId,
        Long doctorProfileId,
        Long familyMemberProfileId,
        String appointmentType,
        String status,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String reason) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/resources/FamilyVisitResource.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record FamilyVisitResource(
        Long id,
        Long patientId,
        Long familyMemberProfileId,
        String appointmentType,
        String status,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String reason) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/resources/MedicalAppointmentResource.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record MedicalAppointmentResource(
        Long id,
        Long patientId,
        Long doctorProfileId,
        String appointmentType,
        String status,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String reason) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/resources/ScheduleFamilyVisitResource.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ScheduleFamilyVisitResource(
        Long patientId,
        Long familyMemberProfileId,
        LocalDateTime startsAt,
        Integer durationInMinutes,
        String reason) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/resources/ScheduleMedicalAppointmentResource.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ScheduleMedicalAppointmentResource(
        Long patientId,
        Long doctorProfileId,
        LocalDateTime startsAt,
        Integer durationInMinutes,
        String reason) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/transform/AppointmentResourceFromEntityAssembler.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.AppointmentResource;

public class AppointmentResourceFromEntityAssembler {

    public static AppointmentResource toResourceFromEntity(Appointment appointment) {
        return new AppointmentResource(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorProfileId(),
                appointment.getFamilyMemberProfileId(),
                appointment.getAppointmentType().name(),
                appointment.getStatus().name(),
                appointment.getTimeSlot().getStartsAt(),
                appointment.getTimeSlot().getEndsAt(),
                appointment.getReason());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/transform/FamilyVisitResourceFromEntityAssembler.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.FamilyVisitResource;

public class FamilyVisitResourceFromEntityAssembler {

    public static FamilyVisitResource toResourceFromEntity(Appointment appointment) {
        return new FamilyVisitResource(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getFamilyMemberProfileId(),
                appointment.getAppointmentType().name(),
                appointment.getStatus().name(),
                appointment.getTimeSlot().getStartsAt(),
                appointment.getTimeSlot().getEndsAt(),
                appointment.getReason());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/transform/MedicalAppointmentResourceFromEntityAssembler.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.aggregates.Appointment;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.MedicalAppointmentResource;

public class MedicalAppointmentResourceFromEntityAssembler {

    public static MedicalAppointmentResource toResourceFromEntity(Appointment appointment) {
        return new MedicalAppointmentResource(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorProfileId(),
                appointment.getAppointmentType().name(),
                appointment.getStatus().name(),
                appointment.getTimeSlot().getStartsAt(),
                appointment.getTimeSlot().getEndsAt(),
                appointment.getReason());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/transform/ScheduleFamilyVisitCommandFromResourceAssembler.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleFamilyVisitCommand;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.ScheduleFamilyVisitResource;

public class ScheduleFamilyVisitCommandFromResourceAssembler {
    public static ScheduleFamilyVisitCommand toCommandFromResource(ScheduleFamilyVisitResource resource) {
        return new ScheduleFamilyVisitCommand(
                resource.patientId(),
                resource.familyMemberProfileId(),
                resource.startsAt(),
                resource.durationInMinutes(),
                resource.reason());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/appointments/interfaces/rest/transform/ScheduleMedicalAppointmentCommandFromResourceAssembler.java`

~~~java
package pe.edu.upc.medibridge.appointments.interfaces.rest.transform;

import pe.edu.upc.medibridge.appointments.domain.model.commands.ScheduleMedicalAppointmentCommand;
import pe.edu.upc.medibridge.appointments.interfaces.rest.resources.ScheduleMedicalAppointmentResource;

public class ScheduleMedicalAppointmentCommandFromResourceAssembler {
    public static ScheduleMedicalAppointmentCommand toCommandFromResource(ScheduleMedicalAppointmentResource resource) {
        return new ScheduleMedicalAppointmentCommand(
                resource.patientId(),
                resource.doctorProfileId(),
                resource.startsAt(),
                resource.durationInMinutes(),
                resource.reason());
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/shared/domain/model/aggregates/AuditableAbstractAggregateRoot.java`

~~~java
package pe.edu.upc.medibridge.shared.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Getter
    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/shared/infrastructure/documentation/openapi/configuration/OpenApiConfiguration.java`

~~~java
package pe.edu.upc.medibridge.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI appointmentsOpenApi() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(apiInfo())
                .externalDocs(externalDocs())
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    private Info apiInfo() {
        return new Info()
                .title("MediBridge Appointments Service API")
                .description("REST API documentation for MediBridge Appointments Service")
                .version("v1.0.0")
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"))
                .contact(new Contact()
                        .name("MediBridge Team"));
    }

    private ExternalDocumentation externalDocs() {
        return new ExternalDocumentation()
                .description("MediBridge Microservices Documentation");
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/shared/interfaces/rest/exception/GlobalExceptionHandler.java`

~~~java
package pe.edu.upc.medibridge.shared.interfaces.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.InvalidAppointmentRequestException;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.InvalidAppointmentTimeSlotException;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.ProfileRelationshipNotAllowedException;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.TimeSlotNotAvailableException;
import pe.edu.upc.medibridge.shared.interfaces.rest.resources.ErrorResponseResource;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            InvalidAppointmentRequestException.class,
            InvalidAppointmentTimeSlotException.class
    })
    public ResponseEntity<ErrorResponseResource> handleBadRequest(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(InvalidPatientReferenceException.class)
    public ResponseEntity<ErrorResponseResource> handleNotFound(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(ProfileRelationshipNotAllowedException.class)
    public ResponseEntity<ErrorResponseResource> handleForbidden(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(TimeSlotNotAvailableException.class)
    public ResponseEntity<ErrorResponseResource> handleConflict(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseResource> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        var details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Request validation failed", request, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseResource> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request or invalid field format",
                request,
                List.of(rootCauseMessage(exception)));
    }

    private ResponseEntity<ErrorResponseResource> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<String> details) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseResource(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        details));
    }

    private String rootCauseMessage(Throwable exception) {
        var cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage() : "Unable to read request body";
    }
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/shared/interfaces/rest/resources/ErrorResponseResource.java`

~~~java
package pe.edu.upc.medibridge.shared.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

public record ErrorResponseResource(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details) {
}
~~~

### `services/appointments-service/src/main/java/pe/edu/upc/medibridge/shared/interfaces/rest/resources/MessageResource.java`

~~~java
package pe.edu.upc.medibridge.shared.interfaces.rest.resources;

public record MessageResource(String message) {
}
~~~

### `services/appointments-service/src/main/resources/application.yml`

~~~yaml
server:
  port: ${PORT:${SERVER_PORT:8084}}

spring:
  application:
    name: appointments-service
  datasource:
    url: ${APPOINTMENTS_DB_URL:jdbc:postgresql://localhost:5433/medibridge_appointments}
    username: ${APPOINTMENTS_DB_USERNAME:postgres}
    password: ${APPOINTMENTS_DB_PASSWORD:12345678}
  jpa:
    hibernate:
      ddl-auto: ${JPA_DDL_AUTO:validate}
    open-in-view: false
    properties:
      hibernate:
        format_sql: true
  flyway:
    enabled: true
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:medibridge}
    password: ${RABBITMQ_PASSWORD:medibridge}
    virtual-host: ${RABBITMQ_VHOST:/}
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 1000ms
          max-attempts: 3
          multiplier: 2
          max-interval: 10000ms
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: ${IAM_JWK_SET_URI:http://localhost:8081/api/v1/jwks/.well-known/jwks.json}

services:
  profiles:
    url: ${PROFILES_SERVICE_URL:http://localhost:8082}

management:
  endpoints:
    web:
      exposure:
        include: health,info

springdoc:
  swagger-ui:
    path: /swagger-ui.html
~~~

### `services/appointments-service/src/main/resources/db/migration/V1__appointments_schema.sql`

~~~sql
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    family_member_profile_id BIGINT,
    doctor_profile_id BIGINT,
    appointment_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    reason VARCHAR(240),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_appointments_patient_starts_at ON appointments(patient_id, starts_at);
CREATE INDEX idx_appointments_patient_status_time ON appointments(patient_id, status, starts_at, ends_at);
~~~

### `services/appointments-service/src/main/resources/db/migration/V2__local_references.sql`

~~~sql
CREATE TABLE patient_references (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE doctor_patient_relations (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL UNIQUE,
    doctor_profile_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_doctor_patient_relations UNIQUE (doctor_profile_id, patient_id)
);

CREATE TABLE family_patient_relations (
    id BIGSERIAL PRIMARY KEY,
    link_id BIGINT NOT NULL UNIQUE,
    family_member_profile_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_family_patient_relations UNIQUE (family_member_profile_id, patient_id)
);

CREATE INDEX idx_doctor_patient_relations_patient_id ON doctor_patient_relations(patient_id);
CREATE INDEX idx_family_patient_relations_patient_id ON family_patient_relations(patient_id);
~~~

