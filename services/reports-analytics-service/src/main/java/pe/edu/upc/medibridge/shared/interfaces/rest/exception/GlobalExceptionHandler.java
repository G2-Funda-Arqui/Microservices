package pe.edu.upc.medibridge.shared.interfaces.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions.ReportGenerationException;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions.ReportNotFoundException;
import pe.edu.upc.medibridge.shared.interfaces.rest.resources.ErrorResponseResource;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            InvalidPatientReferenceException.class,
            ReportNotFoundException.class
    })
    public ResponseEntity<ErrorResponseResource> handleNotFound(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseResource> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request or invalid field format",
                request,
                List.of(rootCauseMessage(exception)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseResource> handleBadRequest(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<ErrorResponseResource> handleReportGeneration(
            ReportGenerationException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request, List.of());
    }

    private ResponseEntity<ErrorResponseResource> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<String> details) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponseResource(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        details));
    }

    private String rootCauseMessage(Throwable exception) {
        var cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage() : "Request could not be processed";
    }
}
