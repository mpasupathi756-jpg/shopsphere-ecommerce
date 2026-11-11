CREATE TABLE product_reviews (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
 customer_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
 order_item_id UUID NOT NULL REFERENCES order_items(id) ON DELETE RESTRICT,
 rating SMALLINT NOT NULL,
 title VARCHAR(120) NOT NULL,
 body VARCHAR(3000) NOT NULL,
 status VARCHAR(16) NOT NULL DEFAULT 'PUBLISHED',
 moderation_reason VARCHAR(500),
 moderated_by UUID REFERENCES users(id) ON DELETE SET NULL,
 moderated_at TIMESTAMPTZ,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_product_reviews_rating CHECK (rating BETWEEN 1 AND 5),
 CONSTRAINT ck_product_reviews_status CHECK (status IN ('PUBLISHED','HIDDEN','REMOVED')),
 CONSTRAINT ux_product_reviews_customer_product UNIQUE(customer_id, product_id)
);
CREATE INDEX idx_product_reviews_product_public ON product_reviews(product_id, created_at DESC) WHERE status='PUBLISHED';
CREATE INDEX idx_product_reviews_customer_created ON product_reviews(customer_id, created_at DESC);
CREATE INDEX idx_product_reviews_moderation ON product_reviews(status, updated_at DESC);

CREATE TABLE product_review_reports (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 review_id UUID NOT NULL REFERENCES product_reviews(id) ON DELETE RESTRICT,
 reporter_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
 reason_code VARCHAR(24) NOT NULL,
 details VARCHAR(500),
 status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
 resolution_note VARCHAR(500),
 resolved_by UUID REFERENCES users(id) ON DELETE SET NULL,
 resolved_at TIMESTAMPTZ,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_review_reports_reason CHECK (reason_code IN ('SPAM','ABUSE','FAKE','IRRELEVANT','OTHER')),
 CONSTRAINT ck_review_reports_status CHECK (status IN ('OPEN','DISMISSED','ACTIONED')),
 CONSTRAINT ux_review_reports_review_reporter UNIQUE(review_id, reporter_id)
);
CREATE INDEX idx_review_reports_queue ON product_review_reports(status, created_at ASC);