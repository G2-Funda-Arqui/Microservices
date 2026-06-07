package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.transform;

import pe.edu.upc.medibridge.healthmonitoring.domain.model.aggregates.PatientHealthObservation;
import pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources.PatientHealthObservationResource;

public class PatientHealthObservationResourceFromEntityAssembler {

    public static PatientHealthObservationResource toResourceFromEntity(PatientHealthObservation entity) {
        return new PatientHealthObservationResource(
                entity.getId(),
                entity.getPatientId(),
                entity.getRecordedByDoctorProfileId(),
                entity.getSystolicBloodPressure(),
                entity.getDiastolicBloodPressure(),
                entity.getBodyTemperature(),
                entity.getPainLevel(),
                entity.getEmotionalState(),
                entity.getEmotionalNotes(),
                entity.getClinicalNotes(),
                entity.getRecordedAt());
    }
}
