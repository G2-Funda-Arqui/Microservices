package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TimeSlotNotAvailableException extends RuntimeException {
    public TimeSlotNotAvailableException(Long patientId) {
        super("Time slot is not available for patient: " + patientId);
    }
}
