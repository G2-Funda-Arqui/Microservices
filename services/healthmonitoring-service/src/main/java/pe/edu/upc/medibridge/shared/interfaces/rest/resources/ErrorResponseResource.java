package pe.edu.upc.medibridge.shared.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ErrorResponseResource(
        LocalDateTime timestamp,
        String message,
        String details
) {
}
