package pe.edu.upc.medibridge.healthmonitoring.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profiles-service", url = "${services.profiles.url}", path = "/api/v1/internal/profiles")
public interface ProfilesServiceClient {
    @GetMapping("/users/{userId}/can-access/{patientId}")
    boolean canUserAccessPatient(@PathVariable Long userId, @PathVariable Long patientId);
}
