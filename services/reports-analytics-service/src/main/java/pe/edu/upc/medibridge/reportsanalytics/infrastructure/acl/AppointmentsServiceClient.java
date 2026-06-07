package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "appointments-service", url = "${services.appointments.url}")
public interface AppointmentsServiceClient {
    @GetMapping("/api/v1/internal/appointments/patients/{patientId}/summary")
    String getAppointmentSummaryByPatientId(
            @PathVariable Long patientId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate);
}
