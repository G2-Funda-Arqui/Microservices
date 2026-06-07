package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import java.time.LocalDate;

public interface ExternalAppointmentService {
    String getAppointmentSummary(Long patientId, LocalDate startDate, LocalDate endDate);
}
