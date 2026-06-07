package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ClinicalLog;

import java.util.List;

public interface ClinicalLogQueryService {
    List<ClinicalLog> findByPatientId(Long patientId);
}
