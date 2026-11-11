INSERT INTO roles (id, code, description, created_at) VALUES (RANDOM_UUID(), 'CUSTOMER', 'Marketplace customer', CURRENT_TIMESTAMP);
INSERT INTO roles (id, code, description, created_at) VALUES (RANDOM_UUID(), 'SELLER', 'Approved marketplace seller', CURRENT_TIMESTAMP);
INSERT INTO roles (id, code, description, created_at) VALUES (RANDOM_UUID(), 'ADMIN', 'Platform administrator', CURRENT_TIMESTAMP);
INSERT INTO categories (id, name, slug, description, status, display_order, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000001', 'Electronics', 'electronics', 'Computers, phones and audio', 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
