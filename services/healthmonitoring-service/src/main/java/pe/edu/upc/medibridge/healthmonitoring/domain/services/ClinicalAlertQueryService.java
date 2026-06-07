package pe.edu.upc.medibridge.healthmonitoring.domain.services;

import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.ClinicalAlert;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetActiveClinicalAlertsByPatientQuery;

import java.util.List;

public interface ClinicalAlertQueryService {
    List<ClinicalAlert> handle(GetActiveClinicalAlertsByPatientQuery query);
}
