package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.transform;

import pe.edu.upc.medibridge.healthmonitoring.domain.model.commands.RecordPatientHealthObservationCommand;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources.RecordPatientHealthObservationResource;

public class RecordPatientHealthObservationCommandFromResourceAssembler {

    public static RecordPatientHealthObservationCommand toCommandFromResource(
            Long patientId,
            RecordPatientHealthObservationResource resource) {
        return new RecordPatientHealthObservationCommand(
                patientId,
                resource.recordedByDoctorProfileId(),
                resource.systolicBloodPressure(),
                resource.diastolicBloodPressure(),
                resource.bodyTemperature(),
                resource.painLevel(),
                resource.emotionalState(),
                resource.emotionalNotes(),
                resource.clinicalNotes(),
                resource.recordedAt());
    }
}
