CREATE TABLE plans (
    id SERIAL PRIMARY KEY,
    commercial_line VARCHAR(30) NOT NULL,
    plan_type VARCHAR(40) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    max_patients INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_plans_line_type_cycle UNIQUE (commercial_line, plan_type, billing_cycle)
);

CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    stripe_customer_id VARCHAR(120) NOT NULL UNIQUE,
    started_at DATE NOT NULL,
    current_period_end DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans(id)
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_user_status ON subscriptions(user_id, status);

CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_invoices_user_issued_at ON invoices(user_id, issued_at DESC);

CREATE TABLE payment_methods (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    brand VARCHAR(30) NOT NULL,
    last_four_digits VARCHAR(4) NOT NULL,
    stripe_payment_method_id VARCHAR(120) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_payment_methods_user_id ON payment_methods(user_id);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    stripe_payment_intent_id VARCHAR(120) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
