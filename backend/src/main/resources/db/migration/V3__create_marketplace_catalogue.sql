CREATE TABLE categories (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 parent_id UUID REFERENCES categories(id) ON DELETE RESTRICT,
 name VARCHAR(100) NOT NULL,
 slug VARCHAR(120) NOT NULL UNIQUE,
 description VARCHAR(500),
 status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
 display_order INTEGER NOT NULL DEFAULT 0,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_categories_status CHECK (status IN ('ACTIVE','INACTIVE'))
);
CREATE INDEX idx_categories_parent_order ON categories(parent_id, display_order);

CREATE TABLE brands (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 name VARCHAR(120) NOT NULL UNIQUE,
 slug VARCHAR(140) NOT NULL UNIQUE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sellers (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE RESTRICT,
 legal_name VARCHAR(180) NOT NULL,
 store_name VARCHAR(120) NOT NULL,
 business_email VARCHAR(254) NOT NULL,
 phone VARCHAR(24) NOT NULL,
 business_address VARCHAR(500) NOT NULL,
 gstin VARCHAR(15),
 status VARCHAR(24) NOT NULL DEFAULT 'PENDING_REVIEW',
 rejection_reason VARCHAR(500),
 submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 reviewed_at TIMESTAMPTZ,
 reviewed_by UUID REFERENCES users(id) ON DELETE SET NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_sellers_status CHECK (status IN ('PENDING_REVIEW','APPROVED','REJECTED','SUSPENDED')),
 CONSTRAINT ck_sellers_gstin CHECK (gstin IS NULL OR gstin ~ '^[0-9A-Z]{15}$')
);
CREATE INDEX idx_sellers_status_submitted ON sellers(status, submitted_at DESC);

CREATE TABLE seller_documents (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 seller_id UUID NOT NULL REFERENCES sellers(id) ON DELETE CASCADE,
 document_type VARCHAR(40) NOT NULL,
 storage_key VARCHAR(500) NOT NULL,
 original_file_name VARCHAR(255) NOT NULL,
 mime_type VARCHAR(100) NOT NULL,
 file_size_bytes BIGINT NOT NULL,
 review_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
 submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 reviewed_at TIMESTAMPTZ,
 CONSTRAINT ck_seller_documents_size CHECK (file_size_bytes > 0),
 CONSTRAINT ck_seller_documents_status CHECK (review_status IN ('PENDING','APPROVED','REJECTED'))
);
CREATE INDEX idx_seller_documents_seller ON seller_documents(seller_id, submitted_at DESC);

CREATE TABLE products (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 category_id UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
 brand_id UUID REFERENCES brands(id) ON DELETE SET NULL,
 created_by_user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
 title VARCHAR(180) NOT NULL,
 slug VARCHAR(200) NOT NULL UNIQUE,
 description TEXT NOT NULL,
 product_status VARCHAR(24) NOT NULL DEFAULT 'PENDING_REVIEW',
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_products_status CHECK (product_status IN ('PENDING_REVIEW','ACTIVE','REJECTED','ARCHIVED'))
);
CREATE INDEX idx_products_category_status ON products(category_id, product_status);
CREATE INDEX idx_products_title ON products USING gin (to_tsvector('simple', title));

CREATE TABLE product_variants (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
 variant_name VARCHAR(120) NOT NULL,
 attributes_json JSONB NOT NULL DEFAULT '{}'::jsonb,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(product_id, variant_name)
);
CREATE INDEX idx_product_variants_product ON product_variants(product_id);

CREATE TABLE product_attributes (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
 attribute_key VARCHAR(80) NOT NULL,
 display_name VARCHAR(100) NOT NULL,
 attribute_value VARCHAR(500) NOT NULL,
 unit VARCHAR(30),
 sort_order INTEGER NOT NULL DEFAULT 0,
 UNIQUE(product_id, attribute_key)
);
CREATE INDEX idx_product_attributes_product ON product_attributes(product_id, sort_order);

CREATE TABLE product_images (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
 storage_key VARCHAR(500) NOT NULL,
 alt_text VARCHAR(180) NOT NULL,
 sort_order INTEGER NOT NULL DEFAULT 0,
 is_primary BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_product_images_product ON product_images(product_id, sort_order);

CREATE TABLE listings (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
 variant_id UUID REFERENCES product_variants(id) ON DELETE RESTRICT,
 seller_id UUID NOT NULL REFERENCES sellers(id) ON DELETE RESTRICT,
 seller_sku VARCHAR(100) NOT NULL,
 price_minor BIGINT NOT NULL,
 currency CHAR(3) NOT NULL DEFAULT 'INR',
 item_condition VARCHAR(20) NOT NULL DEFAULT 'NEW',
 listing_status VARCHAR(24) NOT NULL DEFAULT 'PENDING_REVIEW',
 shipping_days_min SMALLINT NOT NULL DEFAULT 2,
 shipping_days_max SMALLINT NOT NULL DEFAULT 7,
 return_window_days SMALLINT NOT NULL DEFAULT 7,
 warranty_text VARCHAR(300),
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(seller_id, seller_sku),
 CONSTRAINT ck_listings_price CHECK (price_minor > 0),
 CONSTRAINT ck_listings_currency CHECK (currency ~ '^[A-Z]{3}$'),
 CONSTRAINT ck_listings_condition CHECK (item_condition IN ('NEW','REFURBISHED','USED')),
 CONSTRAINT ck_listings_status CHECK (listing_status IN ('PENDING_REVIEW','ACTIVE','PAUSED','REJECTED','SUSPENDED','ARCHIVED')),
 CONSTRAINT ck_listings_shipping CHECK (shipping_days_min >= 0 AND shipping_days_max >= shipping_days_min),
 CONSTRAINT ck_listings_return CHECK (return_window_days >= 0)
);
CREATE INDEX idx_listings_product_status_price ON listings(product_id, listing_status, price_minor);
CREATE INDEX idx_listings_seller_status ON listings(seller_id, listing_status);

CREATE TABLE inventory (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 listing_id UUID NOT NULL UNIQUE REFERENCES listings(id) ON DELETE CASCADE,
 available_qty INTEGER NOT NULL DEFAULT 0,
 reserved_qty INTEGER NOT NULL DEFAULT 0,
 version BIGINT NOT NULL DEFAULT 0,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_inventory_available CHECK (available_qty >= 0),
 CONSTRAINT ck_inventory_reserved CHECK (reserved_qty >= 0)
);

CREATE TABLE inventory_movements (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 inventory_id UUID NOT NULL REFERENCES inventory(id) ON DELETE RESTRICT,
 movement_type VARCHAR(24) NOT NULL,
 quantity_delta INTEGER NOT NULL,
 reference_type VARCHAR(40),
 reference_id UUID,
 note VARCHAR(300),
 created_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_inventory_movement_type CHECK (movement_type IN ('STOCK_RECEIVED','MANUAL_ADJUSTMENT','RESERVED','RELEASED','SOLD','RETURNED'))
);
CREATE INDEX idx_inventory_movements_inventory_time ON inventory_movements(inventory_id, created_at DESC);

INSERT INTO categories(name,slug,description,display_order) VALUES
 ('Electronics','electronics','Computers, phones, audio and accessories',1),
 ('Fashion','fashion','Clothing, footwear and accessories',2),
 ('Home & Living','home-living','Furniture, decor and everyday home essentials',3),
 ('Beauty','beauty','Personal care, skincare and beauty products',4),
 ('Appliances','appliances','Home and kitchen appliances',5),
 ('Sports','sports','Sports, fitness and outdoor equipment',6);
