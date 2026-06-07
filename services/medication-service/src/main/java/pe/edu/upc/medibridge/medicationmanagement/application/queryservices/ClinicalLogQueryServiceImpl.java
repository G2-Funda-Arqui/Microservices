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
