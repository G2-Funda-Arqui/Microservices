package pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl.resources.PatientProfileResponse;

@FeignClient(name = "profiles-service", url = "${services.profiles.url}")
public interface ProfilesServiceClient {
    @GetMapping("/api/v1/internal/profiles/patients/{patientId}/exists")
    boolean patientExists(@PathVariable Long patientId);

    @GetMapping("/api/v1/internal/profiles/patients/{patientId}")
    PatientProfileResponse getPatientProfileById(@PathVariable Long patientId);
}
