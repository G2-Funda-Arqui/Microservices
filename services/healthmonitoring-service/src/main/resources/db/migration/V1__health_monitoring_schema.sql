CREATE TABLE patient_health_observations (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    recorded_by_doctor_profile_id BIGINT NOT NULL,
    systolic_blood_pressure INTEGER NOT NULL,
    diastolic_blood_pressure INTEGER NOT NULL,
    body_temperature NUMERIC(4, 1) NOT NULL,
    pain_level INTEGER NOT NULL,
    emotional_state VARCHAR(30) NOT NULL,
    emotional_notes VARCHAR(240),
    clinical_notes VARCHAR(500),
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_health_observations_patient_recorded_at
    ON patient_health_observations(patient_id, recorded_at DESC);

CREATE TABLE clinical_alerts (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    observation_id BIGINT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    triggered_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_clinical_alerts_patient_status_triggered_at
    ON clinical_alerts(patient_id, status, triggered_at DESC);
