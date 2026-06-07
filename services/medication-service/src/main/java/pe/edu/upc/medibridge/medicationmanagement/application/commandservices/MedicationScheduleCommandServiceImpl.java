package pe.edu.upc.medibridge.medicationmanagement.application.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl.ExternalPatientContextService;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.aggregates.MedicationSchedule;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.CreateMedicationScheduleCommand;
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
            throw new IllegalArgumentException("Patient does not exist: " + command.patientId());
        }
        medicationRepository.findById(command.medicationId())
                .orElseThrow(() -> new MedicationNotFoundException(command.medicationId()));
        return Optional.of(medicationScheduleRepository.save(new MedicationSchedule(command)));
    }
}
