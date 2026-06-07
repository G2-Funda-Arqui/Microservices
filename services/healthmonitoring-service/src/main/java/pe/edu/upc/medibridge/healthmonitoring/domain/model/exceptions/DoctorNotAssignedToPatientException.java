package pe.edu.upc.medibridge.healthmonitoring.domain.model.exceptions;

public class DoctorNotAssignedToPatientException extends RuntimeException {
    public DoctorNotAssignedToPatientException(Long doctorProfileId, Long patientId) {
        super("Doctor profile " + doctorProfileId + " is not assigned to patient " + patientId);
    }
}
