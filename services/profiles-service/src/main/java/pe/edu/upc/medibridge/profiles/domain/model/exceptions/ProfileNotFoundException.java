package pe.edu.upc.medibridge.profiles.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String profileName, Long profileId) {
        super(profileName + " not found: " + profileId);
    }
}
