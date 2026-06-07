package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

public interface ExternalMedicationService {
    String getMedicationSummary(Long patientId);
}
