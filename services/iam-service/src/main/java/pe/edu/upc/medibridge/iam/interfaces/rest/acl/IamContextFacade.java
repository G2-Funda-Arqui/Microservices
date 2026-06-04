package pe.edu.upc.medibridge.iam.interfaces.rest.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.iam.domain.model.queries.GetUserByIdQuery;
import pe.edu.upc.medibridge.iam.domain.services.UserQueryService;

@Service("iamBoundedContextFacade")
public class IamContextFacade {
    private final UserQueryService userQueryService;

    public IamContextFacade(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public boolean userExists(Long userId) {
        return userId != null && userQueryService.handle(new GetUserByIdQuery(userId)).isPresent();
    }

    public String fetchUsernameByUserId(Long userId) {
        return userQueryService.handle(new GetUserByIdQuery(userId))
                .map(user -> user.getUsername())
                .orElse(null);
    }

    public void markSubscriptionActivated(Long userId, Integer subscriptionId) {
        // IAM currently receives SubscriptionActivatedEvent asynchronously.
        // This ACL method keeps the synchronous contract ready for future subscription state.
    }
}
