# Phase 6 — Product Reviews & Ratings

Status: Implemented on Phase 6 branch; CI run 43017417327 passed frontend typecheck/build, backend Maven verification and PostgreSQL 16 Flyway/Hibernate schema validation. Awaiting PR #8 merge.

## Goals
Let customers share useful product feedback after a genuine completed purchase. Keep review eligibility, ownership, aggregation, reporting and moderation under backend control.

## Scope
1. Verified-purchase product reviews.
2. Product rating aggregates and review counts.
3. Customer review history and edit/delete policy.
4. Review reporting and administrator moderation.
5. Product detail and catalogue rating presentation.
6. Automated tests for eligibility, ownership, duplicate prevention, moderation and aggregate correctness.

Seller responses and helpful-vote ranking are deferred until the basic review lifecycle is proven.

## Domain rules
- A review is attached to the canonical product, not a seller listing, because multiple sellers may sell the same product.
- The authenticated customer identity comes only from the verified security principal. Never accept a customer ID as authority from the request body or AI model.
- Eligibility requires an order item for that customer and product whose seller sub-order has reached DELIVERED. Payment-pending, paid-but-unfulfilled, cancelled, returned or merely shipped orders are not eligible.
- Enforce one review per customer per canonical product for the first release. Add a database unique constraint on (customer_id, product_id); application checks alone are insufficient.
- Re-check eligibility and duplicate constraints inside the write transaction.
- Rating is an integer from 1 through 5. Title and body have explicit server-side length limits, normalization and safe output encoding.
- Review creation is allowed only for active customers and products. Moderated/hidden reviews do not contribute to public aggregates.
- Customers may edit their own review. Preserve audit timestamps and re-run validation. A review cannot be edited by another customer or seller.
- Customer deletion policy must preserve order/audit integrity; prefer soft deletion or moderation status rather than hard deletion of historical purchase evidence.
- Reports are unique per reporter/review. A reporter cannot report their own review. Reports do not automatically hide a review.
- Only authorized administrators can hide, restore or remove reviews. Every moderation action records actor, reason and timestamp.
- Aggregate counts and average ratings must exclude hidden/deleted reviews. Avoid exposing private customer/order identifiers.

## Proposed persistence model

### product_reviews
- id — UUID primary key.
- product_id — FK to canonical product.
- customer_id — FK to customer account.
- order_item_id — FK to the delivered order item proving purchase.
- rating — small integer constrained to 1–5.
- title — bounded text.
- body — bounded text.
- status — PUBLISHED, HIDDEN, REMOVED (default PUBLISHED).
- created_at, updated_at — UTC timestamps.
- Unique (customer_id, product_id) and indexes for (product_id, status, created_at) and (customer_id, created_at).
- Validate that order_item_id belongs to the same customer and canonical product. Enforce this in the transactional service; add a database constraint/trigger only if it fits the existing schema conventions.

### product_review_reports
- id — UUID primary key.
- review_id — FK to review.
- reporter_id — FK to customer account.
- reason_code — controlled enum (SPAM, ABUSE, FAKE, IRRELEVANT, OTHER).
- details — optional bounded text.
- status — OPEN, REVIEWED, DISMISSED, ACTIONED.
- created_at, updated_at.
- Unique (review_id, reporter_id) and indexes for moderation queue.

### Aggregates
Prefer deriving aggregates from published reviews with indexed queries initially. If measured query volume justifies denormalization later, maintain count and sum transactionally and reconcile them with a scheduled integrity job. Do not introduce a cached aggregate as the source of truth in this phase.

## API contract (proposed)

Customer:
- POST /api/v1/products/{productSlug}/reviews — create review; eligibility derived from authenticated customer and delivered order items.
- GET /api/v1/products/{productSlug}/reviews — public paginated published reviews, with stable sorting.
- GET /api/v1/me/reviews — authenticated customer's reviews.
- PATCH /api/v1/me/reviews/{reviewId} — edit own review.
- DELETE /api/v1/me/reviews/{reviewId} — soft-delete own review if deletion policy is approved.
- POST /api/v1/reviews/{reviewId}/reports — submit a report.

Admin:
- GET /api/v1/admin/reviews/reports — paginated moderation queue.
- PATCH /api/v1/admin/reviews/{reviewId}/moderation — hide, restore or remove with mandatory reason and audit event.
- PATCH /api/v1/admin/review-reports/{reportId} — resolve report with an explicit outcome.

Public product DTO:
- Include averageRating and reviewCount for published reviews only. Keep average precision stable (e.g. one decimal place); do not return a fake zero rating for products with no reviews (use null and count 0).

## UI
- Product detail: rating summary, review count, paginated review list, empty state and review form only when the signed-in customer is eligible.
- Account: My reviews list with edit/delete actions limited to the customer's own records.
- Admin: report queue, review context and explicit moderation reason/action.
- Catalogue cards: compact rating and count only when reviews exist; avoid clutter for unrated products.
- Accessibility: keyboard-operable forms, labelled rating control, readable validation errors and adequate contrast.

## Security and abuse controls
- Derive identity from JWT principal; do not trust body-supplied customer/order IDs.
- Enforce product/order-item/customer linkage in backend service and transaction.
- Validate request size, field lengths, rating range, pagination bounds and sort allowlist.
- Rate-limit review creation and report submission before public production.
- Encode user-generated text on output; do not render raw HTML.
- Audit moderation actions. Do not leak reporter identity to review authors.
- Add spam/abuse detection as a later controlled layer; do not let an LLM autonomously hide reviews.

## Required tests
1. Customer with a delivered matching order item can create a review.
2. Customer without a matching purchase is rejected.
3. Shipped but not delivered order is rejected.
4. Another customer's order item cannot establish eligibility.
5. Order item for a different product cannot establish eligibility.
6. Duplicate customer/product review is rejected under concurrent requests.
7. Customer cannot edit/delete another customer's review.
8. Invalid ratings, oversized text and invalid pagination are rejected.
9. Hidden/removed reviews are excluded from public listing and aggregates.
10. Reporter cannot report own review or submit duplicate report.
11. Non-admin cannot moderate; moderation action records actor and reason.
12. PostgreSQL Flyway migration and Hibernate schema validation pass.

## Delivery sequence
1. Inspect current order-item/product mappings and status enums; confirm delivered-order join path.
2. Add Flyway migration and review/report entities/repositories.
3. Implement transactional eligibility and review lifecycle services.
4. Add public/customer/admin APIs and authorization tests.
5. Add rating aggregates to product DTOs and UI.
6. Add reporting/moderation UI and tests.
7. Run frontend checks, backend tests and PostgreSQL migration/schema smoke test.
8. Open PR; merge only after green checks and review.

## Dependencies and open decisions
- Confirm exact delivered status and order-item relationship from the existing Phase 4 domain model before writing the eligibility query.
- Confirm whether review deletion is soft-delete (REMOVED) or a separate DELETED status before API implementation.
- No paid moderation service or external AI service is required for this phase.

## Implementation update — 11 November 2026

Implemented on the Phase 6 feature branch (validation is still in progress):

- Flyway V6 creates product_reviews and product_review_reports with foreign keys, rating/status/reason constraints, unique customer/product and reporter/review constraints, and moderation/report queue indexes.
- Review eligibility is derived server-side from a matching order item owned by the authenticated customer and a seller order in DELIVERED status. Customer IDs and order item IDs are not accepted from the review request.
- Customer endpoints: public published-review listing, authenticated eligibility check, create, own-review list/edit/soft-remove, and report.
- Admin endpoints: open report queue, hide/restore/remove with a mandatory reason, and report resolution. Admin routes are protected by the existing ADMIN role matcher.
- Public catalogue card and detail DTOs include published-only averageRating and reviewCount.
- Product detail UI includes rating summary, review pagination, delivered-purchase review form and verified-purchase labels. Account and admin navigation include review management screens.
- Integration coverage added to the marketplace workflow for pre-delivery rejection, post-delivery acceptance, duplicate review rejection, rating aggregates, cross-user edit denial, duplicate report rejection, non-admin moderation denial and admin hide/report resolution.
- CI run for the initial implementation found a Java escaping syntax error in the eligibility response. Fixed in commit 60918f41a90a6424defcbbeb03200b7eaa328abc; a fresh CI run is validating the corrected branch.
- Do not merge until backend Maven tests and PostgreSQL 16 migration/Hibernate schema validation pass.

## Current limitations / production gates

- Review submission/report rate limiting and spam throttling must be configured before public production.
- No automated AI moderation is enabled; administrators make moderation decisions.
- Review text is rendered as normal escaped React text, never as raw HTML.
- Seller replies and helpful votes remain deferred.
