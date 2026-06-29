package pe.edu.upc.medibridge.appointments.application.internal.queryservices;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl.ExternalIamContextService;
import pe.edu.upc.medibridge.appointments.application.internal.outboundservices.acl.ExternalPatientAccessService;
import pe.edu.upc.medibridge.appointments.domain.model.exceptions.PatientAccessDeniedException;

@Service
public class AuthenticatedPatientAccessService {
    private final ExternalIamContextService externalIamContextService;
    private final ExternalPatientAccessService externalPatientAccessService;

    public AuthenticatedPatientAccessService(
            ExternalIamContextService externalIamContextService,
            ExternalPatientAccessService externalPatientAccessService) {
        this.externalIamContextService = externalIamContextService;
        this.externalPatientAccessService = externalPatientAccessService;
    }

    public void requireAccess(Long userId, Long patientId) {
        if (!externalPatientAccessService.canUserAccessPatient(userId, patientId)) {
            throw new PatientAccessDeniedException("Authenticated user cannot access this patient");
        }
    }

    public Long resolveUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new PatientAccessDeniedException("Authenticated user is required");
        }
        return externalIamContextService.findUserIdByUsername(jwt.getSubject())
                .orElseThrow(() -> new PatientAccessDeniedException("Authenticated user was not found"));
    }
}
