package pe.edu.upc.medibridge.profiles.infrastructure.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upc.medibridge.profiles.application.internal.outboundservices.acl.ExternalIamContextService;

@FeignClient(name = "iam-service", url = "${services.iam.url}", path = "/api/v1/internal/users")
public interface IamServiceClient extends ExternalIamContextService {

    @Override
    @GetMapping("/{userId}/exists")
    boolean userExists(@PathVariable Long userId);
}