package pe.edu.upc.medibridge.appointments.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAppointmentTimeSlotException extends RuntimeException {
    public InvalidAppointmentTimeSlotException(String message) {
        super(message);
    }
}
