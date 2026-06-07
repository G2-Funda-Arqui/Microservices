CREATE TABLE analytics_dashboards (
    id SERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE clinical_reports (
    id SERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    report_type VARCHAR(30) NOT NULL,
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    generated_at TIMESTAMP NOT NULL,
    summary VARCHAR(500) NOT NULL,
    pdf_path VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE metric_snapshots (
    id SERIAL PRIMARY KEY,
    analytics_dashboard_id INTEGER,
    patient_id BIGINT NOT NULL,
    metric_type VARCHAR(40) NOT NULL,
    value NUMERIC(10, 2) NOT NULL,
    unit VARCHAR(30) NOT NULL,
    captured_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_metric_snapshots_dashboard FOREIGN KEY (analytics_dashboard_id) REFERENCES analytics_dashboards(id)
);

CREATE TABLE trend_indicators (
    id SERIAL PRIMARY KEY,
    analytics_dashboard_id INTEGER,
    patient_id BIGINT NOT NULL,
    metric_type VARCHAR(40) NOT NULL,
    direction VARCHAR(30) NOT NULL,
    explanation VARCHAR(300) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_trend_indicators_dashboard FOREIGN KEY (analytics_dashboard_id) REFERENCES analytics_dashboards(id)
);

CREATE TABLE report_sections (
    id SERIAL PRIMARY KEY,
    clinical_report_id INTEGER,
    title VARCHAR(120) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    display_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_report_sections_clinical_report FOREIGN KEY (clinical_report_id) REFERENCES clinical_reports(id)
);

CREATE INDEX ix_clinical_reports_patient_generated_at ON clinical_reports(patient_id, generated_at DESC);
CREATE INDEX ix_metric_snapshots_patient_metric ON metric_snapshots(patient_id, metric_type);
CREATE INDEX ix_trend_indicators_patient_metric ON trend_indicators(patient_id, metric_type);
