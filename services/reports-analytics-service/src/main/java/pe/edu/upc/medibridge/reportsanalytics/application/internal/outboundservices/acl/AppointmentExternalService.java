package pe.edu.upc.medibridge.reportsanalytics.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.AppointmentsServiceClient;

import java.time.LocalDate;

@Service
public class AppointmentExternalService implements ExternalAppointmentService {
    private final AppointmentsServiceClient appointmentsServiceClient;

    public AppointmentExternalService(AppointmentsServiceClient appointmentsServiceClient) {
        this.appointmentsServiceClient = appointmentsServiceClient;
    }

    @Override
    public String getAppointmentSummary(Long patientId, LocalDate startDate, LocalDate endDate) {
        return appointmentsServiceClient.getAppointmentSummaryByPatientId(patientId, startDate, endDate);
    }
}
