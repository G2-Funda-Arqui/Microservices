package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import java.time.LocalDate;

public interface ExternalHealthMonitoringService {
    String getPatientClinicalSummary(Long patientId, LocalDate startDate, LocalDate endDate);
}
