CREATE TABLE customer_addresses (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 recipient_name VARCHAR(120) NOT NULL,
 phone VARCHAR(24) NOT NULL,
 address_line1 VARCHAR(180) NOT NULL,
 address_line2 VARCHAR(180),
 city VARCHAR(100) NOT NULL,
 state VARCHAR(100) NOT NULL,
 postal_code VARCHAR(12) NOT NULL,
 country VARCHAR(80) NOT NULL DEFAULT 'India',
 is_default BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_customer_addresses_user ON customer_addresses(user_id, is_default DESC, created_at DESC);

CREATE TABLE carts (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE cart_items (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
 listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE RESTRICT,
 quantity INTEGER NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(cart_id, listing_id),
 CONSTRAINT ck_cart_items_quantity CHECK (quantity > 0 AND quantity <= 20)
);
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);

CREATE TABLE wishlists (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE wishlist_items (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 wishlist_id UUID NOT NULL REFERENCES wishlists(id) ON DELETE CASCADE,
 product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(wishlist_id, product_id)
);

CREATE TABLE orders (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 order_number VARCHAR(32) NOT NULL UNIQUE,
 customer_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
 idempotency_key VARCHAR(80),
 status VARCHAR(24) NOT NULL DEFAULT 'PAYMENT_PENDING',
 currency VARCHAR(3) NOT NULL DEFAULT 'INR',
 subtotal_minor BIGINT NOT NULL,
 shipping_minor BIGINT NOT NULL DEFAULT 0,
 discount_minor BIGINT NOT NULL DEFAULT 0,
 total_minor BIGINT NOT NULL,
 shipping_recipient VARCHAR(120) NOT NULL,
 shipping_phone VARCHAR(24) NOT NULL,
 shipping_line1 VARCHAR(180) NOT NULL,
 shipping_line2 VARCHAR(180),
 shipping_city VARCHAR(100) NOT NULL,
 shipping_state VARCHAR(100) NOT NULL,
 shipping_postal_code VARCHAR(12) NOT NULL,
 shipping_country VARCHAR(80) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_orders_amounts CHECK (subtotal_minor >= 0 AND shipping_minor >= 0 AND discount_minor >= 0 AND total_minor >= 0),
 CONSTRAINT ck_orders_status CHECK (status IN ('PAYMENT_PENDING','PAID','PROCESSING','SHIPPED','DELIVERED','CANCELLED','REFUND_PENDING','REFUNDED'))
);
CREATE UNIQUE INDEX ux_orders_customer_idempotency ON orders(customer_id, idempotency_key) WHERE idempotency_key IS NOT NULL;
CREATE INDEX idx_orders_customer_created ON orders(customer_id, created_at DESC);

CREATE TABLE seller_orders (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 order_id UUID NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
 seller_id UUID NOT NULL REFERENCES sellers(id) ON DELETE RESTRICT,
 status VARCHAR(24) NOT NULL DEFAULT 'PAYMENT_PENDING',
 subtotal_minor BIGINT NOT NULL,
 shipping_minor BIGINT NOT NULL DEFAULT 0,
 total_minor BIGINT NOT NULL,
 tracking_number VARCHAR(100),
 carrier VARCHAR(100),
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_seller_orders_amounts CHECK (subtotal_minor >= 0 AND shipping_minor >= 0 AND total_minor >= 0),
 CONSTRAINT ck_seller_orders_status CHECK (status IN ('PAYMENT_PENDING','PAID','PROCESSING','SHIPPED','DELIVERED','CANCELLED','REFUND_PENDING','REFUNDED'))
);
CREATE INDEX idx_seller_orders_seller_created ON seller_orders(seller_id, created_at DESC);
CREATE INDEX idx_seller_orders_order ON seller_orders(order_id);

CREATE TABLE order_items (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 seller_order_id UUID NOT NULL REFERENCES seller_orders(id) ON DELETE RESTRICT,
 listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE RESTRICT,
 product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
 variant_id UUID REFERENCES product_variants(id) ON DELETE SET NULL,
 seller_id UUID NOT NULL REFERENCES sellers(id) ON DELETE RESTRICT,
 store_name_snapshot VARCHAR(120) NOT NULL,
 product_title_snapshot VARCHAR(180) NOT NULL,
 seller_sku_snapshot VARCHAR(100) NOT NULL,
 quantity INTEGER NOT NULL,
 unit_price_minor BIGINT NOT NULL,
 line_total_minor BIGINT NOT NULL,
 currency VARCHAR(3) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_order_items_quantity CHECK (quantity > 0),
 CONSTRAINT ck_order_items_amounts CHECK (unit_price_minor > 0 AND line_total_minor > 0)
);
CREATE INDEX idx_order_items_seller_order ON order_items(seller_order_id);

CREATE TABLE payments (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 order_id UUID NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
 provider VARCHAR(40) NOT NULL DEFAULT 'UNCONFIGURED',
 provider_reference VARCHAR(180),
 status VARCHAR(24) NOT NULL DEFAULT 'NOT_CONFIGURED',
 amount_minor BIGINT NOT NULL,
 currency VARCHAR(3) NOT NULL DEFAULT 'INR',
 idempotency_key VARCHAR(100) NOT NULL UNIQUE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_payments_amount CHECK (amount_minor >= 0),
 CONSTRAINT ck_payments_status CHECK (status IN ('NOT_CONFIGURED','PENDING','AUTHORIZED','CAPTURED','FAILED','CANCELLED','REFUND_PENDING','REFUNDED'))
);
CREATE INDEX idx_payments_order ON payments(order_id);

CREATE TABLE payment_webhook_events (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 provider VARCHAR(40) NOT NULL,
 event_id VARCHAR(180) NOT NULL,
 payload_hash VARCHAR(64) NOT NULL,
 received_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 processed_at TIMESTAMPTZ,
 processing_status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
 UNIQUE(provider, event_id)
);

CREATE TABLE order_events (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
 from_status VARCHAR(24),
 to_status VARCHAR(24) NOT NULL,
 actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
 note VARCHAR(300),
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_order_events_order_time ON order_events(order_id, created_at DESC);
