package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.queries.GetDoseAdministrationHistoryQuery;

import java.util.List;

public interface DoseAdministrationQueryService {
    List<DoseAdministration> handle(GetDoseAdministrationHistoryQuery query);
}
