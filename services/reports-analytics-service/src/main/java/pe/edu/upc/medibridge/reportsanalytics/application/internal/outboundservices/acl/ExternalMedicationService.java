package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import java.time.LocalDate;

public interface ExternalMedicationService {
    String getMedicationSummary(Long patientId, LocalDate startDate, LocalDate endDate);
}
