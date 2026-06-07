package pe.edu.upc.medibridge.medicationmanagement.application.outboundservices.acl;

public interface ExternalAlertNotificationService {
    void notifyLowStock(Integer medicationId, Long patientId, Integer currentStock);
}
