CREATE TABLE email_verification_token
(
    token      VARCHAR(255) PRIMARY KEY,
    user_id    BIGINT                   NOT NULL,
    expired_at TIMESTAMP WITH TIME ZONE NOT NULL
);
