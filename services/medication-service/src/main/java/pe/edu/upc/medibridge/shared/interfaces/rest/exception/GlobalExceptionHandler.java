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
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.DoseAlreadyAdministeredTodayException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InsufficientStockException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.InvalidPatientReferenceException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationNotFoundException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.MedicationScheduleConflictException;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions.PatientAccessDeniedException;
import pe.edu.upc.medibridge.shared.interfaces.rest.resources.ErrorResponseResource;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            InvalidPatientReferenceException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponseResource> handleInvalidPatientReference(
            RuntimeException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, messageOrDefault(exception, "Resource not found"), request, List.of());
    }

    @ExceptionHandler(MedicationNotFoundException.class)
    public ResponseEntity<ErrorResponseResource> handleMedicationNotFound(
            RuntimeException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler({
            DoseAlreadyAdministeredTodayException.class,
            InsufficientStockException.class,
            MedicationScheduleConflictException.class
    })
    public ResponseEntity<ErrorResponseResource> handleConflict(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorResponseResource> handleBadRequest(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, rootCauseMessage(exception), request, List.of());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseResource> handleIllegalState(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(PatientAccessDeniedException.class)
    public ResponseEntity<ErrorResponseResource> handleForbidden(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseResource> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        var details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Request validation failed", request, details);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseResource> handleUnexpected(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request, List.of());
    }

    private ResponseEntity<ErrorResponseResource> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<String> details) {
        return ResponseEntity.status(status)
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

    private String messageOrDefault(Throwable exception, String defaultMessage) {
        return exception.getMessage() != null ? exception.getMessage() : defaultMessage;
    }
}
