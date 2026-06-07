package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class DoseAlreadyAdministeredTodayException extends RuntimeException {
    public DoseAlreadyAdministeredTodayException(Integer scheduleId) {
        super("Dose already administered today for schedule: " + scheduleId);
    }
}
