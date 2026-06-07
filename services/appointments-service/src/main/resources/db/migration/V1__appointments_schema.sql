CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    family_member_profile_id BIGINT,
    doctor_profile_id BIGINT,
    appointment_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    reason VARCHAR(240),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_appointments_patient_starts_at ON appointments(patient_id, starts_at);
CREATE INDEX idx_appointments_patient_status_time ON appointments(patient_id, status, starts_at, ends_at);
