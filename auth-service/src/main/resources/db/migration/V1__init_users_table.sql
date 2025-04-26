CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    keycloak_id UUID         NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    first_name  VARCHAR(50),
    last_name   VARCHAR(50),
    is_active   BOOLEAN   DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);