package pe.edu.upc.medibridge.healthmonitoring.domain.services;

import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.PatientHealthObservation;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetPatientHealthObservationsQuery;

import java.util.List;

public interface HealthObservationQueryService {
    List<PatientHealthObservation> handle(GetPatientHealthObservationsQuery query);
}
