# medication-service

> Export generado desde el repositorio medibridge. Se excluye target/ porque contiene artefactos compilados.

## Ubicacion en el repositorio

```text
medibridge/
  services/
    medication-service/
```

Ruta relativa: `services/medication-service`

## Estructura del servicio

```text
medication-service/
Dockerfile
pom.xml
src/
  main/
    java/
      pe/
        edu/
          upc/
            medibridge/
              medicationmanagement/
                application/
                  commandservices/
                    DoseAdministrationCommandServiceImpl.java
                    MedicationInventoryCommandServiceImpl.java
                    MedicationScheduleCommandServiceImpl.java
                    ReplenishmentAlertCommandServiceImpl.java
                  eventhandlers/
                    PatientDeactivatedEventHandler.java
                    PatientRegisteredEventHandler.java
                  outboundservices/
                    acl/
                      ExternalAlertNotificationService.java
                      ExternalPatientContextService.java
                  queryservices/
                    ClinicalLogQueryServiceImpl.java
                    DoseAdministrationQueryServiceImpl.java
                    MedicationInventoryQueryServiceImpl.java
                    MedicationScheduleQueryServiceImpl.java
                domain/
                  model/
                    aggregates/
                      MedicationInventory.java
                      MedicationSchedule.java
                    commands/
                      CreateMedicationScheduleCommand.java
                      RecordDoseAdministrationCommand.java
                      RegisterMedicationCommand.java
                      SkipDoseCommand.java
                      TriggerReplenishmentAlertCommand.java
                      UpdateMedicationStockCommand.java
                    entities/
                      ClinicalLog.java
                      DoseAdministration.java
                      Medication.java
                      PatientReference.java
                      ReplenishmentAlert.java
                    events/
                      DoseAdministeredEvent.java
                      DoseSkippedEvent.java
                      MedicationExpiredEvent.java
                      MedicationRegisteredEvent.java
                      StockCriticallyLowEvent.java
                    exceptions/
                      DoseAlreadyAdministeredTodayException.java
                      InsufficientStockException.java
                      InvalidPatientReferenceException.java
                      MedicationNotFoundException.java
                      MedicationScheduleConflictException.java
                    queries/
                      GetActiveMedicationSchedulesQuery.java
                      GetDoseAdministrationHistoryQuery.java
                      GetLowStockMedicationsQuery.java
                      GetMedicationByIdQuery.java
                      GetMedicationsByPatientQuery.java
                    valueobjects/
                      AdministrationRoute.java
                      DosageUnit.java
                      DoseAdministrationStatus.java
                      ExpirationDate.java
                      FrequencyType.java
                      MedicationId.java
                      StockLevel.java
                  services/
                    ClinicalLogQueryService.java
                    DoseAdministrationCommandService.java
                    DoseAdministrationQueryService.java
                    MedicationInventoryCommandService.java
                    MedicationInventoryQueryService.java
                    MedicationScheduleCommandService.java
                    MedicationScheduleQueryService.java
                infrastructure/
                  acl/
                    ProfilesServiceClient.java
                    resources/
                      PatientProfileResponse.java
                  messaging/
                    events/
                      DoseAdministeredIntegrationEvent.java
                      DoseSkippedIntegrationEvent.java
                      MedicationRegisteredIntegrationEvent.java
                      PatientDeactivatedIntegrationEvent.java
                      PatientRegisteredIntegrationEvent.java
                      StockLowIntegrationEvent.java
                    publishers/
                      MedicationIntegrationEventPublisher.java
                    RabbitMQConfiguration.java
                  persistence/
                    jpa/
                      configuration/
                        MedicationPersistenceConfiguration.java
                      repositories/
                        ClinicalLogRepository.java
                        DoseAdministrationRepository.java
                        MedicationInventoryRepository.java
                        MedicationRepository.java
                        MedicationScheduleRepository.java
                        PatientReferenceRepository.java
                        ReplenishmentAlertRepository.java
                  security/
                    SecurityConfiguration.java
                interfaces/
                  rest/
                    acl/
                      MedicationContextFacade.java
                      PatientContextFacade.java
                    controllers/
                      DoseAdministrationController.java
                      MedicationInternalController.java
                      MedicationInventoryController.java
                      MedicationScheduleController.java
                    resources/
                      CreateMedicationScheduleRequest.java
                      DoseAdministrationResponse.java
                      LowStockAlertResponse.java
                      MedicationResponse.java
                      MedicationScheduleResponse.java
                      MedicationSummaryResource.java
                      RecordDoseAdministrationRequest.java
                      RegisterMedicationRequest.java
                      SkipDoseRequest.java
                      UpdateMedicationStockRequest.java
                    transform/
                      CreateMedicationScheduleCommandFromResourceAssembler.java
                      DoseAdministrationResponseFromEntityAssembler.java
                      MedicationResponseFromEntityAssembler.java
                      MedicationScheduleResponseFromEntityAssembler.java
                      RecordDoseAdministrationCommandFromResourceAssembler.java
                      RegisterMedicationCommandFromResourceAssembler.java
                      SkipDoseCommandFromResourceAssembler.java
                MedicationServiceApplication.java
              shared/
                domain/
                  model/
                    aggregates/
                      AuditableAbstractAggregateRoot.java
                    entities/
                      AuditableModel.java
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
          V1__medication_schema.sql
          V2__patient_references.sql
```

## Codigo fuente

### `services/medication-service/Dockerfile`

~~~dockerfile
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /workspace/target/*-SNAPSHOT.jar app.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
~~~

### `services/medication-service/pom.xml`

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
    <artifactId>medication-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>medication-service</name>
    <description>MediBridge Medication Management microservice</description>

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

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/commandservices/DoseAdministrationCommandServiceImpl.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.commandservices;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RecordDoseAdministrationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.SkipDoseCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ClinicalLog;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.DoseAdministeredEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.DoseSkippedEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.StockCriticallyLowEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.DoseAlreadyAdministeredTodayException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InsufficientStockException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationNotFoundException;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationCommandService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.publishers.MedicationIntegrationEventPublisher;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.ClinicalLogRepository;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.DoseAdministrationRepository;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DoseAdministrationStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class DoseAdministrationCommandServiceImpl implements DoseAdministrationCommandService {
    private final DoseAdministrationRepository doseAdministrationRepository;
    private final MedicationRepository medicationRepository;
    private final ClinicalLogRepository clinicalLogRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MedicationIntegrationEventPublisher integrationEventPublisher;

    public DoseAdministrationCommandServiceImpl(
            DoseAdministrationRepository doseAdministrationRepository,
            MedicationRepository medicationRepository,
            ClinicalLogRepository clinicalLogRepository,
            ApplicationEventPublisher eventPublisher,
            MedicationIntegrationEventPublisher integrationEventPublisher) {
        this.doseAdministrationRepository = doseAdministrationRepository;
        this.medicationRepository = medicationRepository;
        this.clinicalLogRepository = clinicalLogRepository;
        this.eventPublisher = eventPublisher;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<DoseAdministration> handle(RecordDoseAdministrationCommand command) {
        ensureDoseWasNotAdministeredToday(command.scheduleId(), command.administeredAt());
        var medication = medicationRepository.findById(command.medicationId())
                .orElseThrow(() -> new MedicationNotFoundException(command.medicationId()));
        if (medication.getStockQuantity() <= 0) {
            throw new InsufficientStockException(command.medicationId());
        }
        medication.decreaseStock();
        medicationRepository.save(medication);

        var doseAdministration = doseAdministrationRepository.save(new DoseAdministration(command));
        clinicalLogRepository.save(new ClinicalLog(
                command.patientId(),
                command.medicationId(),
                "Dose administered for medication " + medication.getName()));
        eventPublisher.publishEvent(new DoseAdministeredEvent(command.medicationId(), command.scheduleId(), command.patientId()));
        integrationEventPublisher.publishDoseAdministered(command.medicationId(), command.scheduleId(), command.patientId());
        if (medication.isLowStock()) {
            eventPublisher.publishEvent(new StockCriticallyLowEvent(medication.getId(), medication.getPatientId(), medication.getStockQuantity()));
            integrationEventPublisher.publishStockLow(medication);
        }
        return Optional.of(doseAdministration);
    }

    @Override
    public Optional<DoseAdministration> handle(SkipDoseCommand command) {
        var doseAdministration = doseAdministrationRepository.save(new DoseAdministration(command));
        clinicalLogRepository.save(new ClinicalLog(
                command.patientId(),
                command.medicationId(),
                "Dose skipped. Reason: " + command.reason()));
        eventPublisher.publishEvent(new DoseSkippedEvent(command.medicationId(), command.scheduleId(), command.patientId()));
        integrationEventPublisher.publishDoseSkipped(command.medicationId(), command.scheduleId(), command.patientId(), command.reason());
        return Optional.of(doseAdministration);
    }

    private void ensureDoseWasNotAdministeredToday(Integer scheduleId, LocalDateTime occurredAt) {
        var date = occurredAt.toLocalDate();
        var start = LocalDateTime.of(date, LocalTime.MIN);
        var end = LocalDateTime.of(date, LocalTime.MAX);
        doseAdministrationRepository.findByScheduleIdAndStatusAndOccurredAtBetween(
                scheduleId,
                DoseAdministrationStatus.ADMINISTERED,
                start,
                end).ifPresent(existing -> {
                    throw new DoseAlreadyAdministeredTodayException(scheduleId);
                });
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/commandservices/MedicationInventoryCommandServiceImpl.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.commandservices;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl.ExternalPatientContextService;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RegisterMedicationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.UpdateMedicationStockCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.MedicationExpiredEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.MedicationRegisteredEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.events.StockCriticallyLowEvent;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationNotFoundException;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationInventoryCommandService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.publishers.MedicationIntegrationEventPublisher;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;

import java.util.Optional;

@Service
public class MedicationInventoryCommandServiceImpl implements MedicationInventoryCommandService {
    private final MedicationRepository medicationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ExternalPatientContextService externalPatientContextService;
    private final MedicationIntegrationEventPublisher integrationEventPublisher;

    public MedicationInventoryCommandServiceImpl(
            MedicationRepository medicationRepository,
            ApplicationEventPublisher eventPublisher,
            ExternalPatientContextService externalPatientContextService,
            MedicationIntegrationEventPublisher integrationEventPublisher) {
        this.medicationRepository = medicationRepository;
        this.eventPublisher = eventPublisher;
        this.externalPatientContextService = externalPatientContextService;
        this.integrationEventPublisher = integrationEventPublisher;
    }

    @Override
    public Optional<Medication> handle(RegisterMedicationCommand command) {
        if (!externalPatientContextService.patientExists(command.patientId())) {
            throw new InvalidPatientReferenceException(command.patientId());
        }
        var medication = medicationRepository.save(new Medication(command));
        eventPublisher.publishEvent(new MedicationRegisteredEvent(medication.getId(), medication.getPatientId()));
        integrationEventPublisher.publishMedicationRegistered(medication);
        if (medication.isLowStock()) {
            eventPublisher.publishEvent(new StockCriticallyLowEvent(medication.getId(), medication.getPatientId(), medication.getStockQuantity()));
            integrationEventPublisher.publishStockLow(medication);
        }
        if (medication.isExpired()) {
            eventPublisher.publishEvent(new MedicationExpiredEvent(medication.getId(), medication.getPatientId()));
        }
        return Optional.of(medication);
    }

    @Override
    public Optional<Medication> handle(UpdateMedicationStockCommand command) {
        var medication = medicationRepository.findById(command.medicationId())
                .orElseThrow(() -> new MedicationNotFoundException(command.medicationId()));
        medication.updateStock(command.stockQuantity());
        var updatedMedication = medicationRepository.save(medication);
        if (updatedMedication.isLowStock()) {
            eventPublisher.publishEvent(new StockCriticallyLowEvent(
                    updatedMedication.getId(),
                    updatedMedication.getPatientId(),
                    updatedMedication.getStockQuantity()));
            integrationEventPublisher.publishStockLow(updatedMedication);
        }
        return Optional.of(updatedMedication);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/commandservices/MedicationScheduleCommandServiceImpl.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl.ExternalPatientContextService;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.CreateMedicationScheduleCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationNotFoundException;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationScheduleCommandService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationScheduleRepository;

import java.util.Optional;

@Service
public class MedicationScheduleCommandServiceImpl implements MedicationScheduleCommandService {
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MedicationRepository medicationRepository;
    private final ExternalPatientContextService externalPatientContextService;

    public MedicationScheduleCommandServiceImpl(
            MedicationScheduleRepository medicationScheduleRepository,
            MedicationRepository medicationRepository,
            ExternalPatientContextService externalPatientContextService) {
        this.medicationScheduleRepository = medicationScheduleRepository;
        this.medicationRepository = medicationRepository;
        this.externalPatientContextService = externalPatientContextService;
    }

    @Override
    public Optional<MedicationSchedule> handle(CreateMedicationScheduleCommand command) {
        if (!externalPatientContextService.patientExists(command.patientId())) {
            throw new InvalidPatientReferenceException(command.patientId());
        }
        medicationRepository.findById(command.medicationId())
                .orElseThrow(() -> new MedicationNotFoundException(command.medicationId()));
        return Optional.of(medicationScheduleRepository.save(new MedicationSchedule(command)));
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/commandservices/ReplenishmentAlertCommandServiceImpl.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.TriggerReplenishmentAlertCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ReplenishmentAlert;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.ReplenishmentAlertRepository;

@Service
public class ReplenishmentAlertCommandServiceImpl {
    private final ReplenishmentAlertRepository replenishmentAlertRepository;

    public ReplenishmentAlertCommandServiceImpl(ReplenishmentAlertRepository replenishmentAlertRepository) {
        this.replenishmentAlertRepository = replenishmentAlertRepository;
    }

    public ReplenishmentAlert handle(TriggerReplenishmentAlertCommand command) {
        return replenishmentAlertRepository.save(new ReplenishmentAlert(
                command.medicationId(),
                command.patientId(),
                command.currentStock()));
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/eventhandlers/PatientDeactivatedEventHandler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.PatientDeactivatedIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Component
public class PatientDeactivatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientDeactivatedEventHandler.class);

    private final PatientReferenceRepository patientReferenceRepository;

    public PatientDeactivatedEventHandler(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PATIENT_DEACTIVATED)
    public void on(PatientDeactivatedIntegrationEvent event) {
        patientReferenceRepository.findByPatientId(event.patientId())
                .ifPresent(reference -> {
                    reference.deactivate();
                    patientReferenceRepository.save(reference);
                });
        LOGGER.info("Patient reference deactivated for medication service: patientId={}", event.patientId());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/eventhandlers/PatientRegisteredEventHandler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.PatientReference;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.PatientRegisteredIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Component
public class PatientRegisteredEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRegisteredEventHandler.class);

    private final PatientReferenceRepository patientReferenceRepository;

    public PatientRegisteredEventHandler(PatientReferenceRepository patientReferenceRepository) {
        this.patientReferenceRepository = patientReferenceRepository;
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PATIENT_REGISTERED)
    public void on(PatientRegisteredIntegrationEvent event) {
        patientReferenceRepository.findByPatientId(event.patientId())
                .ifPresentOrElse(
                        reference -> {
                            reference.reactivate(event.fullName());
                            patientReferenceRepository.save(reference);
                        },
                        () -> patientReferenceRepository.save(new PatientReference(event.patientId(), event.fullName())));
        LOGGER.info("Patient reference synchronized for medication service: patientId={}", event.patientId());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/outboundservices/acl/ExternalAlertNotificationService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl;

public interface ExternalAlertNotificationService {
    void notifyLowStock(Integer medicationId, Long patientId, Integer currentStock);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/outboundservices/acl/ExternalPatientContextService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl;

public interface ExternalPatientContextService {
    boolean patientExists(Long patientId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/queryservices/ClinicalLogQueryServiceImpl.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ClinicalLog;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.ClinicalLogQueryService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.ClinicalLogRepository;

import java.util.List;

@Service
public class ClinicalLogQueryServiceImpl implements ClinicalLogQueryService {
    private final ClinicalLogRepository clinicalLogRepository;

    public ClinicalLogQueryServiceImpl(ClinicalLogRepository clinicalLogRepository) {
        this.clinicalLogRepository = clinicalLogRepository;
    }

    @Override
    public List<ClinicalLog> findByPatientId(Long patientId) {
        return clinicalLogRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/queryservices/DoseAdministrationQueryServiceImpl.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetDoseAdministrationHistoryQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationQueryService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.DoseAdministrationRepository;

import java.util.List;

@Service
public class DoseAdministrationQueryServiceImpl implements DoseAdministrationQueryService {
    private final DoseAdministrationRepository doseAdministrationRepository;

    public DoseAdministrationQueryServiceImpl(DoseAdministrationRepository doseAdministrationRepository) {
        this.doseAdministrationRepository = doseAdministrationRepository;
    }

    @Override
    public List<DoseAdministration> handle(GetDoseAdministrationHistoryQuery query) {
        return doseAdministrationRepository.findByMedicationIdOrderByOccurredAtDesc(query.medicationId());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/queryservices/MedicationInventoryQueryServiceImpl.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetLowStockMedicationsQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationByIdQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationsByPatientQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationInventoryQueryService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MedicationInventoryQueryServiceImpl implements MedicationInventoryQueryService {
    private final MedicationRepository medicationRepository;

    public MedicationInventoryQueryServiceImpl(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @Override
    public Optional<Medication> handle(GetMedicationByIdQuery query) {
        return medicationRepository.findById(query.medicationId());
    }

    @Override
    public List<Medication> handle(GetMedicationsByPatientQuery query) {
        return medicationRepository.findByPatientIdAndActiveTrue(query.patientId());
    }

    @Override
    public List<Medication> handle(GetLowStockMedicationsQuery query) {
        return medicationRepository.findByPatientIdAndActiveTrue(query.patientId()).stream()
                .filter(Medication::isLowStock)
                .toList();
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/application/queryservices/MedicationScheduleQueryServiceImpl.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.application.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetActiveMedicationSchedulesQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationScheduleQueryService;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationScheduleRepository;

import java.util.List;

@Service
public class MedicationScheduleQueryServiceImpl implements MedicationScheduleQueryService {
    private final MedicationScheduleRepository medicationScheduleRepository;

    public MedicationScheduleQueryServiceImpl(MedicationScheduleRepository medicationScheduleRepository) {
        this.medicationScheduleRepository = medicationScheduleRepository;
    }

    @Override
    public List<MedicationSchedule> handle(GetActiveMedicationSchedulesQuery query) {
        return medicationScheduleRepository.findByPatientIdAndActiveTrue(query.patientId());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/aggregates/MedicationInventory.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class MedicationInventory extends AuditableAbstractAggregateRoot<MedicationInventory> {
    @Column(nullable = false, unique = true)
    private Long patientId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inventory_id")
    private List<Medication> medications = new ArrayList<>();

    public MedicationInventory(Long patientId) {
        this.patientId = patientId;
    }

    public void addMedication(Medication medication) {
        this.medications.add(medication);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/aggregates/MedicationSchedule.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.CreateMedicationScheduleCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.FrequencyType;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor
public class MedicationSchedule extends AuditableAbstractAggregateRoot<MedicationSchedule> {
    @Column(nullable = false)
    private Integer medicationId;

    @Column(nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FrequencyType frequencyType;

    @Column(nullable = false)
    private Integer timesPerDay;

    @Column(nullable = false)
    private LocalTime administrationTime;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active;

    public MedicationSchedule(CreateMedicationScheduleCommand command) {
        this.medicationId = command.medicationId();
        this.patientId = command.patientId();
        this.frequencyType = command.frequencyType();
        this.timesPerDay = command.timesPerDay();
        this.administrationTime = command.administrationTime();
        this.startDate = command.startDate();
        this.endDate = command.endDate();
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/commands/CreateMedicationScheduleCommand.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.FrequencyType;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateMedicationScheduleCommand(
        Integer medicationId,
        Long patientId,
        FrequencyType frequencyType,
        Integer timesPerDay,
        LocalTime administrationTime,
        LocalDate startDate,
        LocalDate endDate) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/commands/RecordDoseAdministrationCommand.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

import java.time.LocalDateTime;

public record RecordDoseAdministrationCommand(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime administeredAt,
        String notes) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/commands/RegisterMedicationCommand.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.AdministrationRoute;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DosageUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterMedicationCommand(
        Long patientId,
        String name,
        BigDecimal dosageAmount,
        DosageUnit dosageUnit,
        AdministrationRoute administrationRoute,
        Integer stockQuantity,
        Integer lowStockThreshold,
        LocalDate expirationDate) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/commands/SkipDoseCommand.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

import java.time.LocalDateTime;

public record SkipDoseCommand(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime skippedAt,
        String reason) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/commands/TriggerReplenishmentAlertCommand.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

public record TriggerReplenishmentAlertCommand(Integer medicationId, Long patientId, Integer currentStock) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/commands/UpdateMedicationStockCommand.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.commands;

public record UpdateMedicationStockCommand(Integer medicationId, Integer stockQuantity) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/entities/ClinicalLog.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

@Getter
@Entity
@NoArgsConstructor
public class ClinicalLog extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long patientId;

    private Integer medicationId;

    @Column(nullable = false, length = 500)
    private String description;

    public ClinicalLog(Long patientId, Integer medicationId, String description) {
        this.patientId = patientId;
        this.medicationId = medicationId;
        this.description = description;
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/entities/DoseAdministration.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RecordDoseAdministrationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.SkipDoseCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DoseAdministrationStatus;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class DoseAdministration extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer medicationId;

    @Column(nullable = false)
    private Integer scheduleId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DoseAdministrationStatus status;

    @Column(length = 250)
    private String notes;

    public DoseAdministration(RecordDoseAdministrationCommand command) {
        this.medicationId = command.medicationId();
        this.scheduleId = command.scheduleId();
        this.patientId = command.patientId();
        this.occurredAt = command.administeredAt();
        this.status = DoseAdministrationStatus.ADMINISTERED;
        this.notes = command.notes();
    }

    public DoseAdministration(SkipDoseCommand command) {
        this.medicationId = command.medicationId();
        this.scheduleId = command.scheduleId();
        this.patientId = command.patientId();
        this.occurredAt = command.skippedAt();
        this.status = DoseAdministrationStatus.SKIPPED;
        this.notes = command.reason();
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/entities/Medication.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RegisterMedicationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.AdministrationRoute;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DosageUnit;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
public class Medication extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private Long patientId;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dosageAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DosageUnit dosageUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AdministrationRoute administrationRoute;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer stockQuantity;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer lowStockThreshold;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private boolean active;

    public Medication(RegisterMedicationCommand command) {
        this.patientId = command.patientId();
        this.name = command.name();
        this.dosageAmount = command.dosageAmount();
        this.dosageUnit = command.dosageUnit();
        this.administrationRoute = command.administrationRoute();
        this.stockQuantity = command.stockQuantity();
        this.lowStockThreshold = command.lowStockThreshold();
        this.expirationDate = command.expirationDate();
        this.active = true;
    }

    public void updateStock(Integer stockQuantity) {
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = stockQuantity;
    }

    public void decreaseStock() {
        if (stockQuantity <= 0) {
            throw new IllegalStateException("Medication stock is insufficient");
        }
        this.stockQuantity--;
    }

    public boolean isLowStock() {
        return stockQuantity <= lowStockThreshold;
    }

    public boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/entities/PatientReference.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "patient_references")
public class PatientReference extends AuditableAbstractAggregateRoot<PatientReference> {

    @Column(nullable = false, unique = true)
    private Long patientId;

    @Column(nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false)
    private boolean active;

    public PatientReference(Long patientId, String fullName) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.active = true;
    }

    public void reactivate(String fullName) {
        this.fullName = fullName;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/entities/ReplenishmentAlert.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

@Getter
@Entity
@NoArgsConstructor
public class ReplenishmentAlert extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer medicationId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private boolean resolved;

    public ReplenishmentAlert(Integer medicationId, Long patientId, Integer currentStock) {
        this.medicationId = medicationId;
        this.patientId = patientId;
        this.currentStock = currentStock;
        this.resolved = false;
    }

    public void resolve() {
        this.resolved = true;
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/events/DoseAdministeredEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record DoseAdministeredEvent(Integer medicationId, Integer scheduleId, Long patientId, Instant occurredAt, int version) {
    public DoseAdministeredEvent(Integer medicationId, Integer scheduleId, Long patientId) {
        this(medicationId, scheduleId, patientId, Instant.now(), 1);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/events/DoseSkippedEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record DoseSkippedEvent(Integer medicationId, Integer scheduleId, Long patientId, Instant occurredAt, int version) {
    public DoseSkippedEvent(Integer medicationId, Integer scheduleId, Long patientId) {
        this(medicationId, scheduleId, patientId, Instant.now(), 1);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/events/MedicationExpiredEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record MedicationExpiredEvent(Integer medicationId, Long patientId, Instant occurredAt, int version) {
    public MedicationExpiredEvent(Integer medicationId, Long patientId) {
        this(medicationId, patientId, Instant.now(), 1);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/events/MedicationRegisteredEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record MedicationRegisteredEvent(Integer medicationId, Long patientId, Instant occurredAt, int version) {
    public MedicationRegisteredEvent(Integer medicationId, Long patientId) {
        this(medicationId, patientId, Instant.now(), 1);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/events/StockCriticallyLowEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.events;

import java.time.Instant;

public record StockCriticallyLowEvent(Integer medicationId, Long patientId, Integer currentStock, Instant occurredAt, int version) {
    public StockCriticallyLowEvent(Integer medicationId, Long patientId, Integer currentStock) {
        this(medicationId, patientId, currentStock, Instant.now(), 1);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/exceptions/DoseAlreadyAdministeredTodayException.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class DoseAlreadyAdministeredTodayException extends RuntimeException {
    public DoseAlreadyAdministeredTodayException(Integer scheduleId) {
        super("Dose already administered today for schedule: " + scheduleId);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/exceptions/InsufficientStockException.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Integer medicationId) {
        super("Insufficient stock for medication: " + medicationId);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/exceptions/InvalidPatientReferenceException.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class InvalidPatientReferenceException extends RuntimeException {
    public InvalidPatientReferenceException(Long patientId) {
        super("Patient reference not found: " + patientId);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/exceptions/MedicationNotFoundException.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class MedicationNotFoundException extends RuntimeException {
    public MedicationNotFoundException(Integer medicationId) {
        super("Medication not found with id: " + medicationId);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/exceptions/MedicationScheduleConflictException.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class MedicationScheduleConflictException extends RuntimeException {
    public MedicationScheduleConflictException(Integer medicationId) {
        super("Medication schedule conflict for medication: " + medicationId);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/queries/GetActiveMedicationSchedulesQuery.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.queries;

public record GetActiveMedicationSchedulesQuery(Long patientId) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/queries/GetDoseAdministrationHistoryQuery.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.queries;

public record GetDoseAdministrationHistoryQuery(Integer medicationId) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/queries/GetLowStockMedicationsQuery.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.queries;

public record GetLowStockMedicationsQuery(Long patientId) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/queries/GetMedicationByIdQuery.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.queries;

public record GetMedicationByIdQuery(Integer medicationId) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/queries/GetMedicationsByPatientQuery.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.queries;

public record GetMedicationsByPatientQuery(Long patientId) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/valueobjects/AdministrationRoute.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

public enum AdministrationRoute {
    ORAL,
    IV,
    IM,
    SUBCUTANEOUS,
    TOPICAL
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/valueobjects/DosageUnit.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

public enum DosageUnit {
    MG,
    ML,
    TABLET,
    CAPSULE,
    DROP,
    UNIT
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/valueobjects/DoseAdministrationStatus.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

public enum DoseAdministrationStatus {
    ADMINISTERED,
    SKIPPED
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/valueobjects/ExpirationDate.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

import java.time.LocalDate;

public record ExpirationDate(LocalDate value) {
    public ExpirationDate {
        if (value == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }
    }

    public boolean isExpired() {
        return value.isBefore(LocalDate.now());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/valueobjects/FrequencyType.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

public enum FrequencyType {
    DAILY,
    TWICE_DAILY,
    WEEKLY,
    AS_NEEDED
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/valueobjects/MedicationId.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

public record MedicationId(Integer value) {
    public MedicationId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Medication id must be a positive number");
        }
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/model/valueobjects/StockLevel.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

public record StockLevel(Integer quantity, Integer lowStockThreshold) {
    public StockLevel {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        if (lowStockThreshold == null || lowStockThreshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
    }

    public boolean isLow() {
        return quantity <= lowStockThreshold;
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/services/ClinicalLogQueryService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ClinicalLog;

import java.util.List;

public interface ClinicalLogQueryService {
    List<ClinicalLog> findByPatientId(Long patientId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/services/DoseAdministrationCommandService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RecordDoseAdministrationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.SkipDoseCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;

import java.util.Optional;

public interface DoseAdministrationCommandService {
    Optional<DoseAdministration> handle(RecordDoseAdministrationCommand command);
    Optional<DoseAdministration> handle(SkipDoseCommand command);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/services/DoseAdministrationQueryService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetDoseAdministrationHistoryQuery;

import java.util.List;

public interface DoseAdministrationQueryService {
    List<DoseAdministration> handle(GetDoseAdministrationHistoryQuery query);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/services/MedicationInventoryCommandService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RegisterMedicationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.UpdateMedicationStockCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;

import java.util.Optional;

public interface MedicationInventoryCommandService {
    Optional<Medication> handle(RegisterMedicationCommand command);
    Optional<Medication> handle(UpdateMedicationStockCommand command);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/services/MedicationInventoryQueryService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetLowStockMedicationsQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationByIdQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationsByPatientQuery;

import java.util.List;
import java.util.Optional;

public interface MedicationInventoryQueryService {
    Optional<Medication> handle(GetMedicationByIdQuery query);
    List<Medication> handle(GetMedicationsByPatientQuery query);
    List<Medication> handle(GetLowStockMedicationsQuery query);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/services/MedicationScheduleCommandService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.CreateMedicationScheduleCommand;

import java.util.Optional;

public interface MedicationScheduleCommandService {
    Optional<MedicationSchedule> handle(CreateMedicationScheduleCommand command);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/domain/services/MedicationScheduleQueryService.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetActiveMedicationSchedulesQuery;

import java.util.List;

public interface MedicationScheduleQueryService {
    List<MedicationSchedule> handle(GetActiveMedicationSchedulesQuery query);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/acl/ProfilesServiceClient.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.acl.resources.PatientProfileResponse;

@FeignClient(name = "profiles-service", url = "${services.profiles.url}")
public interface ProfilesServiceClient {
    @GetMapping("/api/v1/internal/profiles/patients/{patientId}/exists")
    boolean patientExists(@PathVariable Long patientId);

    @GetMapping("/api/v1/internal/profiles/patients/{patientId}")
    PatientProfileResponse getPatientProfileById(@PathVariable Long patientId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/acl/resources/PatientProfileResponse.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.acl.resources;

public record PatientProfileResponse(Long id, String fullName) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/messaging/events/DoseAdministeredIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record DoseAdministeredIntegrationEvent(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        Instant occurredAt,
        int version) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/messaging/events/DoseSkippedIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record DoseSkippedIntegrationEvent(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        String reason,
        Instant occurredAt,
        int version) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/messaging/events/MedicationRegisteredIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record MedicationRegisteredIntegrationEvent(
        Integer medicationId,
        Long patientId,
        String medicationName,
        Instant occurredAt,
        int version) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/messaging/events/PatientDeactivatedIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record PatientDeactivatedIntegrationEvent(
        Long patientId,
        Instant occurredAt,
        int version) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/messaging/events/PatientRegisteredIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record PatientRegisteredIntegrationEvent(
        Long patientId,
        String fullName,
        Instant occurredAt,
        int version) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/messaging/events/StockLowIntegrationEvent.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events;

import java.time.Instant;

public record StockLowIntegrationEvent(
        Integer medicationId,
        Long patientId,
        String medicationName,
        Integer currentStock,
        Integer threshold,
        Instant occurredAt,
        int version) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/messaging/publishers/MedicationIntegrationEventPublisher.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.DoseAdministeredIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.DoseSkippedIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.MedicationRegisteredIntegrationEvent;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging.events.StockLowIntegrationEvent;

import java.time.Instant;

@Component
public class MedicationIntegrationEventPublisher {
    private static final int VERSION = 1;

    private final RabbitTemplate rabbitTemplate;

    public MedicationIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMedicationRegistered(Medication medication) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.ROUTING_KEY_MEDICATION_REGISTERED,
                new MedicationRegisteredIntegrationEvent(
                        medication.getId(),
                        medication.getPatientId(),
                        medication.getName(),
                        Instant.now(),
                        VERSION));
    }

    public void publishDoseAdministered(Integer medicationId, Integer scheduleId, Long patientId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.ROUTING_KEY_DOSE_ADMINISTERED,
                new DoseAdministeredIntegrationEvent(medicationId, scheduleId, patientId, Instant.now(), VERSION));
    }

    public void publishDoseSkipped(Integer medicationId, Integer scheduleId, Long patientId, String reason) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.ROUTING_KEY_DOSE_SKIPPED,
                new DoseSkippedIntegrationEvent(medicationId, scheduleId, patientId, reason, Instant.now(), VERSION));
    }

    public void publishStockLow(Medication medication) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.ROUTING_KEY_STOCK_LOW,
                new StockLowIntegrationEvent(
                        medication.getId(),
                        medication.getPatientId(),
                        medication.getName(),
                        medication.getStockQuantity(),
                        medication.getLowStockThreshold(),
                        Instant.now(),
                        VERSION));
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/messaging/RabbitMQConfiguration.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.messaging;

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
    public static final String ROUTING_KEY_MEDICATION_REGISTERED = "medication.registered";
    public static final String ROUTING_KEY_DOSE_ADMINISTERED = "dose.administered";
    public static final String ROUTING_KEY_DOSE_SKIPPED = "dose.skipped";
    public static final String ROUTING_KEY_STOCK_LOW = "stock.low";

    public static final String QUEUE_PATIENT_REGISTERED = "medication.patient-registered";
    public static final String QUEUE_PATIENT_DEACTIVATED = "medication.patient-deactivated";

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
    public Binding patientRegisteredBinding(Queue patientRegisteredQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(patientRegisteredQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_PATIENT_REGISTERED);
    }

    @Bean
    public Binding patientDeactivatedBinding(Queue patientDeactivatedQueue, TopicExchange medibridgeEventsExchange) {
        return BindingBuilder.bind(patientDeactivatedQueue).to(medibridgeEventsExchange).with(ROUTING_KEY_PATIENT_DEACTIVATED);
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

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/persistence/jpa/configuration/MedicationPersistenceConfiguration.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MedicationPersistenceConfiguration {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/persistence/jpa/repositories/ClinicalLogRepository.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ClinicalLog;

import java.util.List;

@Repository
public interface ClinicalLogRepository extends JpaRepository<ClinicalLog, Integer> {
    List<ClinicalLog> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/persistence/jpa/repositories/DoseAdministrationRepository.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DoseAdministrationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoseAdministrationRepository extends JpaRepository<DoseAdministration, Integer> {
    List<DoseAdministration> findByMedicationIdOrderByOccurredAtDesc(Integer medicationId);
    long countByPatientId(Long patientId);
    Optional<DoseAdministration> findByScheduleIdAndStatusAndOccurredAtBetween(
            Integer scheduleId,
            DoseAdministrationStatus status,
            LocalDateTime start,
            LocalDateTime end);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/persistence/jpa/repositories/MedicationInventoryRepository.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationInventory;

import java.util.Optional;

@Repository
public interface MedicationInventoryRepository extends JpaRepository<MedicationInventory, Integer> {
    Optional<MedicationInventory> findByPatientId(Long patientId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/persistence/jpa/repositories/MedicationRepository.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Integer> {
    List<Medication> findByPatientId(Long patientId);
    List<Medication> findByPatientIdAndActiveTrue(Long patientId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/persistence/jpa/repositories/MedicationScheduleRepository.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;

import java.util.List;

@Repository
public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Integer> {
    List<MedicationSchedule> findByPatientIdAndActiveTrue(Long patientId);
    List<MedicationSchedule> findByMedicationIdAndActiveTrue(Integer medicationId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/persistence/jpa/repositories/PatientReferenceRepository.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.PatientReference;

import java.util.Optional;

@Repository
public interface PatientReferenceRepository extends JpaRepository<PatientReference, Integer> {
    boolean existsByPatientIdAndActiveTrue(Long patientId);
    Optional<PatientReference> findByPatientId(Long patientId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/persistence/jpa/repositories/ReplenishmentAlertRepository.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ReplenishmentAlert;

import java.util.List;

@Repository
public interface ReplenishmentAlertRepository extends JpaRepository<ReplenishmentAlert, Integer> {
    List<ReplenishmentAlert> findByPatientIdAndResolvedFalse(Long patientId);
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/infrastructure/security/SecurityConfiguration.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.infrastructure.security;

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

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/acl/MedicationContextFacade.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationsByPatientQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationInventoryQueryService;

import java.util.stream.Collectors;

@Service
public class MedicationContextFacade {
    private final MedicationInventoryQueryService medicationInventoryQueryService;

    public MedicationContextFacade(MedicationInventoryQueryService medicationInventoryQueryService) {
        this.medicationInventoryQueryService = medicationInventoryQueryService;
    }

    public String fetchMedicationSummaryByPatientId(Long patientId) {
        var medications = medicationInventoryQueryService.handle(new GetMedicationsByPatientQuery(patientId));
        if (medications.isEmpty()) {
            return "No active medications registered for this patient.";
        }
        return medications.stream()
                .map(medication -> {
                    var stockStatus = medication.isLowStock()
                            ? "low stock"
                            : "stock available";
                    var expirationStatus = medication.isExpired()
                            ? "expired"
                            : "expires on " + medication.getExpirationDate();
                    return medication.getName()
                            + ": " + medication.getDosageAmount().stripTrailingZeros().toPlainString()
                            + " " + medication.getDosageUnit()
                            + " via " + medication.getAdministrationRoute()
                            + ". Stock: " + medication.getStockQuantity()
                            + " units, threshold: " + medication.getLowStockThreshold()
                            + " (" + stockStatus + "). " + expirationStatus + ".";
                })
                .collect(Collectors.joining(" "));
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/acl/PatientContextFacade.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.acl;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl.ExternalPatientContextService;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.PatientReference;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.acl.ProfilesServiceClient;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.PatientReferenceRepository;

@Service
public class PatientContextFacade implements ExternalPatientContextService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientContextFacade.class);

    private final PatientReferenceRepository patientReferenceRepository;
    private final ProfilesServiceClient profilesServiceClient;

    public PatientContextFacade(
            PatientReferenceRepository patientReferenceRepository,
            ProfilesServiceClient profilesServiceClient) {
        this.patientReferenceRepository = patientReferenceRepository;
        this.profilesServiceClient = profilesServiceClient;
    }

    @Override
    public boolean patientExists(Long patientId) {
        if (patientId == null) {
            return false;
        }
        if (patientReferenceRepository.existsByPatientIdAndActiveTrue(patientId)) {
            return true;
        }
        return synchronizePatientReferenceFromProfiles(patientId);
    }

    private boolean synchronizePatientReferenceFromProfiles(Long patientId) {
        try {
            if (!profilesServiceClient.patientExists(patientId)) {
                return false;
            }
            synchronizePatientReference(patientId);
            return true;
        } catch (FeignException.NotFound exception) {
            return false;
        } catch (FeignException exception) {
            LOGGER.warn(
                    "Could not verify patient {} against profiles-service. status={}",
                    patientId,
                    exception.status());
            return false;
        }
    }

    private void synchronizePatientReference(Long patientId) {
        try {
            var profile = profilesServiceClient.getPatientProfileById(patientId);
            var fullName = profile == null || profile.fullName() == null || profile.fullName().isBlank()
                    ? "Patient " + patientId
                    : profile.fullName();

            patientReferenceRepository.findByPatientId(patientId)
                    .ifPresentOrElse(
                            reference -> {
                                reference.reactivate(fullName);
                                patientReferenceRepository.save(reference);
                            },
                            () -> patientReferenceRepository.save(new PatientReference(patientId, fullName)));
        } catch (FeignException exception) {
            LOGGER.warn(
                    "Patient {} exists in profiles-service, but medication-service could not refresh its local reference. status={}",
                    patientId,
                    exception.status());
        }
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/controllers/DoseAdministrationController.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetDoseAdministrationHistoryQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationCommandService;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.DoseAdministrationQueryService;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.DoseAdministrationResponse;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.RecordDoseAdministrationRequest;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.SkipDoseRequest;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.DoseAdministrationResponseFromEntityAssembler;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.RecordDoseAdministrationCommandFromResourceAssembler;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.SkipDoseCommandFromResourceAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/dose-administrations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dose Administrations", description = "Dose Administration Management Endpoints")
public class DoseAdministrationController {
    private final DoseAdministrationCommandService doseAdministrationCommandService;
    private final DoseAdministrationQueryService doseAdministrationQueryService;

    public DoseAdministrationController(
            DoseAdministrationCommandService doseAdministrationCommandService,
            DoseAdministrationQueryService doseAdministrationQueryService) {
        this.doseAdministrationCommandService = doseAdministrationCommandService;
        this.doseAdministrationQueryService = doseAdministrationQueryService;
    }

    @PostMapping
    public ResponseEntity<DoseAdministrationResponse> recordDoseAdministration(
            @RequestBody RecordDoseAdministrationRequest resource) {
        var command = RecordDoseAdministrationCommandFromResourceAssembler.toCommandFromResource(resource);
        var doseAdministration = doseAdministrationCommandService.handle(command);
        return doseAdministration
                .map(value -> new ResponseEntity<>(
                        DoseAdministrationResponseFromEntityAssembler.toResourceFromEntity(value),
                        HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/skip")
    public ResponseEntity<DoseAdministrationResponse> skipDose(@RequestBody SkipDoseRequest resource) {
        var command = SkipDoseCommandFromResourceAssembler.toCommandFromResource(resource);
        var doseAdministration = doseAdministrationCommandService.handle(command);
        return doseAdministration
                .map(value -> new ResponseEntity<>(
                        DoseAdministrationResponseFromEntityAssembler.toResourceFromEntity(value),
                        HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/medications/{medicationId}")
    public ResponseEntity<List<DoseAdministrationResponse>> getDoseAdministrationHistory(
            @PathVariable Integer medicationId) {
        var doseAdministrations = doseAdministrationQueryService.handle(
                new GetDoseAdministrationHistoryQuery(medicationId));
        var resources = doseAdministrations.stream()
                .map(DoseAdministrationResponseFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/controllers/MedicationInternalController.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.DoseAdministrationRepository;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.MedicationRepository;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationSummaryResource;

@RestController
@RequestMapping(value = "/api/v1/internal/medications", produces = MediaType.APPLICATION_JSON_VALUE)
public class MedicationInternalController {
    private final MedicationRepository medicationRepository;
    private final DoseAdministrationRepository doseAdministrationRepository;

    public MedicationInternalController(
            MedicationRepository medicationRepository,
            DoseAdministrationRepository doseAdministrationRepository) {
        this.medicationRepository = medicationRepository;
        this.doseAdministrationRepository = doseAdministrationRepository;
    }

    @GetMapping("/patients/{patientId}/summary")
    public ResponseEntity<MedicationSummaryResource> getMedicationSummary(@PathVariable Long patientId) {
        var activeMedications = medicationRepository.findByPatientIdAndActiveTrue(patientId);
        var lowStockMedications = activeMedications.stream()
                .filter(medication -> medication.getStockQuantity() <= medication.getLowStockThreshold())
                .toList();
        var doseAdministrations = doseAdministrationRepository.countByPatientId(patientId);

        return ResponseEntity.ok(new MedicationSummaryResource(
                patientId,
                activeMedications.size(),
                lowStockMedications.size(),
                doseAdministrations));
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/controllers/MedicationInventoryController.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.UpdateMedicationStockCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetLowStockMedicationsQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationByIdQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetMedicationsByPatientQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationInventoryCommandService;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationInventoryQueryService;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.LowStockAlertResponse;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationResponse;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.RegisterMedicationRequest;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.UpdateMedicationStockRequest;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.MedicationResponseFromEntityAssembler;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.RegisterMedicationCommandFromResourceAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/medications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Medication Inventory", description = "Medication Inventory Management Endpoints")
public class MedicationInventoryController {
    private final MedicationInventoryCommandService medicationInventoryCommandService;
    private final MedicationInventoryQueryService medicationInventoryQueryService;

    public MedicationInventoryController(
            MedicationInventoryCommandService medicationInventoryCommandService,
            MedicationInventoryQueryService medicationInventoryQueryService) {
        this.medicationInventoryCommandService = medicationInventoryCommandService;
        this.medicationInventoryQueryService = medicationInventoryQueryService;
    }

    @PostMapping
    public ResponseEntity<MedicationResponse> registerMedication(@RequestBody RegisterMedicationRequest resource) {
        var command = RegisterMedicationCommandFromResourceAssembler.toCommandFromResource(resource);
        var medication = medicationInventoryCommandService.handle(command);
        return medication
                .map(value -> new ResponseEntity<>(
                        MedicationResponseFromEntityAssembler.toResourceFromEntity(value),
                        HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{medicationId}")
    public ResponseEntity<MedicationResponse> getMedicationById(@PathVariable Integer medicationId) {
        var medication = medicationInventoryQueryService.handle(new GetMedicationByIdQuery(medicationId));
        return medication
                .map(value -> ResponseEntity.ok(MedicationResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<List<MedicationResponse>> getMedicationsByPatient(@PathVariable Long patientId) {
        var medications = medicationInventoryQueryService.handle(new GetMedicationsByPatientQuery(patientId));
        var resources = medications.stream()
                .map(MedicationResponseFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{medicationId}/stock")
    public ResponseEntity<MedicationResponse> updateMedicationStock(
            @PathVariable Integer medicationId,
            @RequestBody UpdateMedicationStockRequest resource) {
        var medication = medicationInventoryCommandService.handle(
                new UpdateMedicationStockCommand(medicationId, resource.stockQuantity()));
        return medication
                .map(value -> ResponseEntity.ok(MedicationResponseFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/patients/{patientId}/low-stock")
    public ResponseEntity<List<LowStockAlertResponse>> getLowStockMedications(@PathVariable Long patientId) {
        var medications = medicationInventoryQueryService.handle(new GetLowStockMedicationsQuery(patientId));
        var resources = medications.stream()
                .map(MedicationResponseFromEntityAssembler::toLowStockResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/controllers/MedicationScheduleController.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetActiveMedicationSchedulesQuery;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationScheduleCommandService;
import pe.edu.upc.medibridge.medicationmanagement.domain.services.MedicationScheduleQueryService;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.CreateMedicationScheduleRequest;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationScheduleResponse;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.CreateMedicationScheduleCommandFromResourceAssembler;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform.MedicationScheduleResponseFromEntityAssembler;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/medication-schedules", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Medication Schedules", description = "Medication Schedule Management Endpoints")
public class MedicationScheduleController {
    private final MedicationScheduleCommandService medicationScheduleCommandService;
    private final MedicationScheduleQueryService medicationScheduleQueryService;

    public MedicationScheduleController(
            MedicationScheduleCommandService medicationScheduleCommandService,
            MedicationScheduleQueryService medicationScheduleQueryService) {
        this.medicationScheduleCommandService = medicationScheduleCommandService;
        this.medicationScheduleQueryService = medicationScheduleQueryService;
    }

    @PostMapping
    public ResponseEntity<MedicationScheduleResponse> createSchedule(@RequestBody CreateMedicationScheduleRequest resource) {
        var command = CreateMedicationScheduleCommandFromResourceAssembler.toCommandFromResource(resource);
        var schedule = medicationScheduleCommandService.handle(command);
        return schedule
                .map(value -> new ResponseEntity<>(
                        MedicationScheduleResponseFromEntityAssembler.toResourceFromEntity(value),
                        HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/patients/{patientId}/active")
    public ResponseEntity<List<MedicationScheduleResponse>> getActiveSchedulesByPatient(@PathVariable Long patientId) {
        var schedules = medicationScheduleQueryService.handle(new GetActiveMedicationSchedulesQuery(patientId));
        var resources = schedules.stream()
                .map(MedicationScheduleResponseFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/CreateMedicationScheduleRequest.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.FrequencyType;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateMedicationScheduleRequest(
        Integer medicationId,
        Long patientId,
        FrequencyType frequencyType,
        Integer timesPerDay,
        LocalTime administrationTime,
        LocalDate startDate,
        LocalDate endDate) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/DoseAdministrationResponse.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DoseAdministrationStatus;

import java.time.LocalDateTime;

public record DoseAdministrationResponse(
        Integer id,
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime occurredAt,
        DoseAdministrationStatus status,
        String notes) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/LowStockAlertResponse.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

public record LowStockAlertResponse(Integer medicationId, Long patientId, String medicationName, Integer currentStock, Integer threshold) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/MedicationResponse.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.AdministrationRoute;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DosageUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedicationResponse(
        Integer id,
        Long patientId,
        String name,
        BigDecimal dosageAmount,
        DosageUnit dosageUnit,
        AdministrationRoute administrationRoute,
        Integer stockQuantity,
        Integer lowStockThreshold,
        LocalDate expirationDate,
        boolean active) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/MedicationScheduleResponse.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.FrequencyType;

import java.time.LocalDate;
import java.time.LocalTime;

public record MedicationScheduleResponse(
        Integer id,
        Integer medicationId,
        Long patientId,
        FrequencyType frequencyType,
        Integer timesPerDay,
        LocalTime administrationTime,
        LocalDate startDate,
        LocalDate endDate,
        boolean active) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/MedicationSummaryResource.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

public record MedicationSummaryResource(
        Long patientId,
        int activeMedications,
        int lowStockMedications,
        long doseAdministrations) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/RecordDoseAdministrationRequest.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import java.time.LocalDateTime;

public record RecordDoseAdministrationRequest(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime administeredAt,
        String notes) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/RegisterMedicationRequest.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.AdministrationRoute;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects.DosageUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterMedicationRequest(
        Long patientId,
        String name,
        BigDecimal dosageAmount,
        DosageUnit dosageUnit,
        AdministrationRoute administrationRoute,
        Integer stockQuantity,
        Integer lowStockThreshold,
        LocalDate expirationDate) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/SkipDoseRequest.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

import java.time.LocalDateTime;

public record SkipDoseRequest(
        Integer medicationId,
        Integer scheduleId,
        Long patientId,
        LocalDateTime skippedAt,
        String reason) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/resources/UpdateMedicationStockRequest.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources;

public record UpdateMedicationStockRequest(Integer stockQuantity) {
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/transform/CreateMedicationScheduleCommandFromResourceAssembler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.CreateMedicationScheduleCommand;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.CreateMedicationScheduleRequest;

public class CreateMedicationScheduleCommandFromResourceAssembler {
    public static CreateMedicationScheduleCommand toCommandFromResource(CreateMedicationScheduleRequest resource) {
        return new CreateMedicationScheduleCommand(
                resource.medicationId(),
                resource.patientId(),
                resource.frequencyType(),
                resource.timesPerDay(),
                resource.administrationTime(),
                resource.startDate(),
                resource.endDate());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/transform/DoseAdministrationResponseFromEntityAssembler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.DoseAdministrationResponse;

public class DoseAdministrationResponseFromEntityAssembler {
    public static DoseAdministrationResponse toResourceFromEntity(DoseAdministration entity) {
        return new DoseAdministrationResponse(
                entity.getId(),
                entity.getMedicationId(),
                entity.getScheduleId(),
                entity.getPatientId(),
                entity.getOccurredAt(),
                entity.getStatus(),
                entity.getNotes());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/transform/MedicationResponseFromEntityAssembler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.Medication;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.LowStockAlertResponse;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationResponse;

public class MedicationResponseFromEntityAssembler {
    public static MedicationResponse toResourceFromEntity(Medication entity) {
        return new MedicationResponse(
                entity.getId(),
                entity.getPatientId(),
                entity.getName(),
                entity.getDosageAmount(),
                entity.getDosageUnit(),
                entity.getAdministrationRoute(),
                entity.getStockQuantity(),
                entity.getLowStockThreshold(),
                entity.getExpirationDate(),
                entity.isActive());
    }

    public static LowStockAlertResponse toLowStockResourceFromEntity(Medication entity) {
        return new LowStockAlertResponse(
                entity.getId(),
                entity.getPatientId(),
                entity.getName(),
                entity.getStockQuantity(),
                entity.getLowStockThreshold());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/transform/MedicationScheduleResponseFromEntityAssembler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.MedicationScheduleResponse;

public class MedicationScheduleResponseFromEntityAssembler {
    public static MedicationScheduleResponse toResourceFromEntity(MedicationSchedule entity) {
        return new MedicationScheduleResponse(
                entity.getId(),
                entity.getMedicationId(),
                entity.getPatientId(),
                entity.getFrequencyType(),
                entity.getTimesPerDay(),
                entity.getAdministrationTime(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isActive());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/transform/RecordDoseAdministrationCommandFromResourceAssembler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RecordDoseAdministrationCommand;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.RecordDoseAdministrationRequest;

public class RecordDoseAdministrationCommandFromResourceAssembler {
    public static RecordDoseAdministrationCommand toCommandFromResource(RecordDoseAdministrationRequest resource) {
        return new RecordDoseAdministrationCommand(
                resource.medicationId(),
                resource.scheduleId(),
                resource.patientId(),
                resource.administeredAt(),
                resource.notes());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/transform/RegisterMedicationCommandFromResourceAssembler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RegisterMedicationCommand;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.RegisterMedicationRequest;

public class RegisterMedicationCommandFromResourceAssembler {
    public static RegisterMedicationCommand toCommandFromResource(RegisterMedicationRequest resource) {
        return new RegisterMedicationCommand(
                resource.patientId(),
                resource.name(),
                resource.dosageAmount(),
                resource.dosageUnit(),
                resource.administrationRoute(),
                resource.stockQuantity(),
                resource.lowStockThreshold(),
                resource.expirationDate());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/interfaces/rest/transform/SkipDoseCommandFromResourceAssembler.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.transform;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.SkipDoseCommand;
import pe.edu.upc.medibridge.medicationmanagement.interfaces.rest.resources.SkipDoseRequest;

public class SkipDoseCommandFromResourceAssembler {
    public static SkipDoseCommand toCommandFromResource(SkipDoseRequest resource) {
        return new SkipDoseCommand(
                resource.medicationId(),
                resource.scheduleId(),
                resource.patientId(),
                resource.skippedAt(),
                resource.reason());
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/medicationmanagement/MedicationServiceApplication.java`

~~~java
package pe.edu.upc.medibridge.medicationmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication(scanBasePackages = "pe.edu.upc.medibridge")
public class MedicationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicationServiceApplication.class, args);
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/shared/domain/model/aggregates/AuditableAbstractAggregateRoot.java`

~~~java
package pe.edu.upc.medibridge.shared.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableAbstractAggregateRoot<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;

    public Integer getId() {
        return id;
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/shared/domain/model/entities/AuditableModel.java`

~~~java
package pe.edu.upc.medibridge.shared.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditableModel {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Date updatedAt;
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/shared/infrastructure/documentation/openapi/configuration/OpenApiConfiguration.java`

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
    public OpenAPI medicationOpenApi() {
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
                .title("MediBridge Medication Service API")
                .description("REST API documentation for MediBridge Medication Service")
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

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/shared/interfaces/rest/exception/GlobalExceptionHandler.java`

~~~java
package pe.edu.upc.medibridge.shared.interfaces.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.DoseAlreadyAdministeredTodayException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InsufficientStockException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationNotFoundException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationScheduleConflictException;
import pe.edu.upc.medibridge.shared.interfaces.rest.resources.ErrorResponseResource;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPatientReferenceException.class)
    public ResponseEntity<ErrorResponseResource> handleInvalidPatientReference(
            RuntimeException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(MedicationNotFoundException.class)
    public ResponseEntity<ErrorResponseResource> handleMedicationNotFound(
            RuntimeException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler({
            DoseAlreadyAdministeredTodayException.class,
            InsufficientStockException.class,
            MedicationScheduleConflictException.class
    })
    public ResponseEntity<ErrorResponseResource> handleConflict(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorResponseResource> handleBadRequest(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, rootCauseMessage(exception), request, List.of());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseResource> handleIllegalState(RuntimeException exception, HttpServletRequest request) {
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
        return cause.getMessage() != null ? cause.getMessage() : "Request could not be processed";
    }
}
~~~

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/shared/interfaces/rest/resources/ErrorResponseResource.java`

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

### `services/medication-service/src/main/java/pe/edu/upc/medibridge/shared/interfaces/rest/resources/MessageResource.java`

~~~java
package pe.edu.upc.medibridge.shared.interfaces.rest.resources;

public record MessageResource(String message) {
}
~~~

### `services/medication-service/src/main/resources/application.yml`

~~~yaml
server:
  port: ${PORT:${SERVER_PORT:8086}}

spring:
  application:
    name: medication-service
  datasource:
    url: ${MEDICATION_DB_URL:jdbc:postgresql://localhost:5433/medibridge_medication}
    username: ${MEDICATION_DB_USERNAME:postgres}
    password: ${MEDICATION_DB_PASSWORD:12345678}
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

### `services/medication-service/src/main/resources/db/migration/V1__medication_schema.sql`

~~~sql
CREATE TABLE medication (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    dosage_amount NUMERIC(10, 2) NOT NULL,
    dosage_unit VARCHAR(20) NOT NULL,
    administration_route VARCHAR(30) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    low_stock_threshold INTEGER NOT NULL,
    expiration_date DATE NOT NULL,
    active BOOLEAN NOT NULL,
    inventory_id INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_medication_stock_non_negative CHECK (stock_quantity >= 0),
    CONSTRAINT chk_medication_low_stock_threshold_non_negative CHECK (low_stock_threshold >= 0)
);

CREATE TABLE medication_inventory (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE medication
    ADD CONSTRAINT fk_medication_inventory
    FOREIGN KEY (inventory_id) REFERENCES medication_inventory(id);

CREATE TABLE medication_schedule (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    medication_id INTEGER NOT NULL,
    patient_id BIGINT NOT NULL,
    frequency_type VARCHAR(30) NOT NULL,
    times_per_day INTEGER NOT NULL,
    administration_time TIME NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_medication_schedule_medication
        FOREIGN KEY (medication_id) REFERENCES medication(id)
);

CREATE TABLE dose_administration (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    medication_id INTEGER NOT NULL,
    schedule_id INTEGER NOT NULL,
    patient_id BIGINT NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes VARCHAR(250),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_dose_administration_medication
        FOREIGN KEY (medication_id) REFERENCES medication(id),
    CONSTRAINT fk_dose_administration_schedule
        FOREIGN KEY (schedule_id) REFERENCES medication_schedule(id)
);

CREATE TABLE clinical_log (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    medication_id INTEGER,
    description VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_clinical_log_medication
        FOREIGN KEY (medication_id) REFERENCES medication(id)
);

CREATE TABLE replenishment_alert (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    medication_id INTEGER NOT NULL,
    patient_id BIGINT NOT NULL,
    current_stock INTEGER NOT NULL,
    resolved BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_replenishment_alert_medication
        FOREIGN KEY (medication_id) REFERENCES medication(id)
);

CREATE INDEX idx_medication_patient_id ON medication(patient_id);
CREATE INDEX idx_medication_active_patient_id ON medication(patient_id, active);
CREATE INDEX idx_medication_schedule_patient_active ON medication_schedule(patient_id, active);
CREATE INDEX idx_dose_administration_medication_occurred_at ON dose_administration(medication_id, occurred_at DESC);
CREATE INDEX idx_clinical_log_patient_created_at ON clinical_log(patient_id, created_at DESC);
CREATE INDEX idx_replenishment_alert_patient_resolved ON replenishment_alert(patient_id, resolved);
~~~

### `services/medication-service/src/main/resources/db/migration/V2__patient_references.sql`

~~~sql
CREATE TABLE patient_references (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(160) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_patient_references_patient_active ON patient_references(patient_id, active);
~~~

