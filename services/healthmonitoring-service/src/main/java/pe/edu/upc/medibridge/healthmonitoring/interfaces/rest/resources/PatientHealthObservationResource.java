package pe.edu.upc.medibridge.healthmonitoring.interfaces.rest.resources;

import pe.edu.upc.medibridge.healthmonitoring.domain.model.valueobjects.EmotionalState;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PatientHealthObservationResource(
        Long id,
        Long patientId,
        Long recordedByDoctorProfileId,
        Integer systolicBloodPressure,
        Integer diastolicBloodPressure,
        BigDecimal bodyTemperature,
        Integer painLevel,
        EmotionalState emotionalState,
        String emotionalNotes,
        String clinicalNotes,
        LocalDateTime recordedAt) {
}
