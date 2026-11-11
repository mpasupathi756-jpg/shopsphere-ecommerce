CREATE TABLE roles (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 code VARCHAR(40) NOT NULL UNIQUE,
 description VARCHAR(160),
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE users (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 email VARCHAR(254) NOT NULL UNIQUE,
 password_hash VARCHAR(100) NOT NULL,
 display_name VARCHAR(120) NOT NULL,
 account_status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
 email_verified BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_users_status CHECK (account_status IN ('PENDING','ACTIVE','LOCKED','DISABLED'))
);
CREATE TABLE user_roles (
 user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 role_id UUID NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
 assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (user_id, role_id)
);
INSERT INTO roles(code,description) VALUES ('CUSTOMER','Marketplace customer'),('SELLER','Approved marketplace seller'),('ADMIN','Platform administrator');
CREATE INDEX idx_users_status_created ON users(account_status,created_at DESC);
