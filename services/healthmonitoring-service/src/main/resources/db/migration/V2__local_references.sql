CREATE TABLE patient_references (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE doctor_patient_relations (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL UNIQUE,
    doctor_profile_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_health_doctor_patient_relations UNIQUE (doctor_profile_id, patient_id)
);

CREATE INDEX idx_health_doctor_patient_relations_patient_id
    ON doctor_patient_relations(patient_id);
