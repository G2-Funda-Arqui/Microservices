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
