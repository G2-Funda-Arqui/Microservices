package pe.edu.upc.medibridge.healthmonitoring.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.healthmonitoring.application.internal.outboundservices.acl.ExternalProfilesContextService;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.ClinicalAlert;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.queries.GetActiveClinicalAlertsByPatientQuery;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.AlertStatus;
import pe.edu.upc.medibridge.healthmonitoring.domain.services.ClinicalAlertQueryService;
import pe.edu.upc.medibridge.healthmonitoring.infrastructure.persistence.jpa.repositories.ClinicalAlertRepository;

import java.util.List;

@Service
public class ClinicalAlertQueryServiceImpl implements ClinicalAlertQueryService {

    private final ClinicalAlertRepository clinicalAlertRepository;
    private final ExternalProfilesContextService externalProfilesContextService;
    private final AuthenticatedPatientAccessService authenticatedPatientAccessService;

    public ClinicalAlertQueryServiceImpl(
            ClinicalAlertRepository clinicalAlertRepository,
            ExternalProfilesContextService externalProfilesContextService,
            AuthenticatedPatientAccessService authenticatedPatientAccessService) {
        this.clinicalAlertRepository = clinicalAlertRepository;
        this.externalProfilesContextService = externalProfilesContextService;
        this.authenticatedPatientAccessService = authenticatedPatientAccessService;
    }

    @Override
    public List<ClinicalAlert> handle(GetActiveClinicalAlertsByPatientQuery query) {
        if (!externalProfilesContextService.patientExists(query.patientId())) {
            throw new InvalidPatientReferenceException(query.patientId());
        }
        if (query.requestedByUserId() != null) {
            authenticatedPatientAccessService.requireAccess(query.requestedByUserId(), query.patientId());
        }
        return clinicalAlertRepository.findByPatientIdAndStatusOrderByTriggeredAtDesc(
                query.patientId(),
                AlertStatus.ACTIVE);
    }
}
