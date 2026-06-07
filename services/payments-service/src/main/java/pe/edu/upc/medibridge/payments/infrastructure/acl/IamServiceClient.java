package pe.edu.upc.medibridge.payments.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service", url = "${services.iam.url}", path = "/api/v1/internal/users")
public interface IamServiceClient {

    @GetMapping("/{userId}/exists")
    boolean userExists(@PathVariable Long userId);
}
