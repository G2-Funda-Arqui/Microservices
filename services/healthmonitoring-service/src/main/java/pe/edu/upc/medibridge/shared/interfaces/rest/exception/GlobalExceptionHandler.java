package pe.edu.upc.medibridge.shared.interfaces.rest.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.DoctorNotAssignedToPatientException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.InvalidHealthObservationException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.PatientAccessDeniedException;
import pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions.PremiumSubscriptionRequiredException;
import pe.edu.upc.medibridge.shared.interfaces.rest.resources.ErrorResponseResource;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            InvalidHealthObservationException.class,
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponseResource> handleBadRequest(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, requestErrorMessage(exception), request);
    }

    @ExceptionHandler({
            InvalidPatientReferenceException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponseResource> handleNotFound(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, messageOrDefault(exception, "Resource not found"), request);
    }

    @ExceptionHandler({
            DoctorNotAssignedToPatientException.class,
            PatientAccessDeniedException.class,
            PremiumSubscriptionRequiredException.class
    })
    public ResponseEntity<ErrorResponseResource> handleForbidden(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseResource> handleConflict(DataIntegrityViolationException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, rootCauseMessage(exception), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseResource> handleUnexpected(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request);
    }

    private ResponseEntity<ErrorResponseResource> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseResource(LocalDateTime.now(), message, request.getRequestURI()));
    }

    private String requestErrorMessage(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException validationException) {
            return validationException.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .orElse("Request validation failed");
        }
        if (exception instanceof HttpMessageNotReadableException) {
            return "Malformed JSON request or invalid field format";
        }
        return rootCauseMessage(exception);
    }

    private String messageOrDefault(Throwable exception, String defaultMessage) {
        return exception.getMessage() != null ? exception.getMessage() : defaultMessage;
    }

    private String rootCauseMessage(Throwable exception) {
        var cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage() : "Request could not be processed";
    }
}
