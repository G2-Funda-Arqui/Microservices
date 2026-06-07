package pe.edu.upc.medibridge.shared.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

public record ErrorResponseResource(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details) {
}
