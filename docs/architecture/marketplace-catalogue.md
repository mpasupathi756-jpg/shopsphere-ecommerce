# Marketplace catalogue and seller lifecycle — Phase 2

## Seller onboarding

1. An authenticated customer submits one seller application.
2. The application remains PENDING_REVIEW and does not grant seller permissions.
3. An administrator reviews the business details and approves or rejects the application.
4. Approval assigns the SELLER role in the user_roles join table.
5. The seller refreshes their access token to receive the new role claim.
6. Seller APIs require both the SELLER JWT authority and an APPROVED seller record.

Seller documents have a metadata table, but upload and verification storage integration are not implemented yet. GSTIN is optional at this milestone; production KYC and tax checks remain required.

## Product and offer model

- Category and Brand classify the canonical Product.
- Product is a shared catalogue identity, independent of any single seller.
- ProductVariant captures a variant identity and structured attributes.
- ProductAttribute stores searchable/displayable specifications.
- ProductImage stores object-storage keys and alt text; actual upload is a later integration.
- Seller owns a Listing (offer) for an approved Product.
- Listing stores seller SKU, price in integer paise, currency, condition, delivery window, return window and moderation status.
- Inventory is one-to-one with Listing and tracks available and reserved quantities.
- InventoryMovement records stock receipts and future reservation/release/sale adjustments.

## Moderation lifecycle

- Seller application: PENDING_REVIEW → APPROVED or REJECTED.
- Product submission: PENDING_REVIEW → ACTIVE or REJECTED.
- Listing submission: PENDING_REVIEW → ACTIVE or REJECTED.
- A listing can only be approved when both its seller and product are approved.
- Pausing is restricted to the owning seller and only active listings can be paused.
- Public catalogue search returns ACTIVE products with at least one ACTIVE listing.

## API boundaries

### Public
- GET /api/v1/catalog/categories
- GET /api/v1/catalog/products?q=&category=&brand=&page=&size=
- GET /api/v1/catalog/products/{slug}

### Customer / seller application
- POST /api/v1/seller-applications
- GET /api/v1/seller-applications/me

### Seller (requires SELLER role and approved seller record)
- POST /api/v1/seller/products
- GET /api/v1/seller/products
- POST /api/v1/seller/listings
- GET /api/v1/seller/listings
- PATCH /api/v1/seller/listings/{id}/pause

### Admin (requires ADMIN role)
- GET /api/v1/admin/sellers?status=PENDING_REVIEW
- PATCH /api/v1/admin/sellers/{id}/approve
- PATCH /api/v1/admin/sellers/{id}/reject
- GET /api/v1/admin/catalog/products/pending
- PATCH /api/v1/admin/catalog/products/{id}/approve
- PATCH /api/v1/admin/catalog/products/{id}/reject
- GET /api/v1/admin/catalog/listings/pending
- PATCH /api/v1/admin/catalog/listings/{id}/approve
- PATCH /api/v1/admin/catalog/listings/{id}/reject

## Operational and security notes

- Optional local/controlled admin bootstrap uses ADMIN_BOOTSTRAP_EMAIL and ADMIN_BOOTSTRAP_PASSWORD. Do not put a real password in committed files; configure it through a secret manager.
- Public registration cannot assign SELLER or ADMIN roles.
- Price values are integer paise; the frontend formats INR only for display.
- Listing SKU uniqueness is scoped to seller.
- Product and listing moderation reasons are persisted.
- Public product cards and detail pages use actual catalogue API data. Product images are still placeholders until a storage adapter is connected.
- The current CI uses embedded H2 to exercise application workflows without Docker. Flyway V3 PostgreSQL syntax and production indexes still require verification against a real PostgreSQL instance before deployment.
