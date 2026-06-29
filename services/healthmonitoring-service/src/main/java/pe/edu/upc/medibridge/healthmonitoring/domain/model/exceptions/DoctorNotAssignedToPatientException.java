package pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DoctorNotAssignedToPatientException extends RuntimeException {
    public DoctorNotAssignedToPatientException(Long doctorProfileId, Long patientId) {
        super("Doctor profile " + doctorProfileId + " is not assigned to patient " + patientId);
    }
}
