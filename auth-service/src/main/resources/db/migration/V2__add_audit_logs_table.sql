CREATE TABLE audit_logs
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    action     VARCHAR(100) NOT NULL,
    details    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
