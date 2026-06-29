package pe.edu.upc.medibridge.profiles.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.medibridge.profiles.infrastructure.acl.resources.IamUserResponse;

@FeignClient(name = "iam-service", url = "${services.iam.url}", path = "/api/v1/internal/users")
public interface IamServiceClient {

    @GetMapping("/{userId}/exists")
    boolean userExists(@PathVariable Long userId);

    @GetMapping("/by-username/{username}")
    IamUserResponse getUserByUsername(@PathVariable String username);
}
