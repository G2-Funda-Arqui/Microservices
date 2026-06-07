package pe.edu.upc.medibridge.healthmonitoring.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.healthmonitoring.application.internal.outboundservices.acl.ExternalProfilesContextService;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.PatientHealthObservation;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetPatientHealthObservationsQuery;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.HealthObservationQueryService;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.PatientHealthObservationRepository;

import java.util.List;

@Service
public class HealthObservationQueryServiceImpl implements HealthObservationQueryService {

    private final PatientHealthObservationRepository patientHealthObservationRepository;
    private final ExternalProfilesContextService externalProfilesContextService;

    public HealthObservationQueryServiceImpl(
            PatientHealthObservationRepository patientHealthObservationRepository,
            ExternalProfilesContextService externalProfilesContextService) {
        this.patientHealthObservationRepository = patientHealthObservationRepository;
        this.externalProfilesContextService = externalProfilesContextService;
    }

    @Override
    public List<PatientHealthObservation> handle(GetPatientHealthObservationsQuery query) {
        if (!externalProfilesContextService.patientExists(query.patientId())) {
            throw new InvalidPatientReferenceException(query.patientId());
        }
        return patientHealthObservationRepository.findByPatientIdOrderByRecordedAtDesc(query.patientId());
    }
}
