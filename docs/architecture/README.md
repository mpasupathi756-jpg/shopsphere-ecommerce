# Architecture baseline

ShopSphere starts as a modular monolith. The frontend is a separate React application; the backend is one Spring Boot application with explicit business module boundaries.

## Request and persistence flow
Browser → React feature → typed API client → REST controller → application service → Spring Data repository → Hibernate/JPA → PostgreSQL.

Controllers validate transport input. Application services own use cases and transactions. Repository interfaces express persistence operations. Business rules do not belong in React or in the LLM.

## Marketplace model
Product is the canonical catalogue identity. A Seller publishes a Listing (offer) for a Product or ProductVariant. Inventory is listing-specific. Cart lines reference listings. At checkout, a parent Order is split into seller-owned SubOrders; OrderItems preserve purchased price and seller snapshots.

## Security status
Phase 0 endpoints are scaffolding only. The current security chain is intentionally permissive to support early UI/API integration. It MUST be replaced with authentication, role checks and seller/customer ownership enforcement before real user data or commerce endpoints are exposed. Do not treat this branch as production-ready.

## Accepted decisions
- ADR-0001: modular monolith first.
- ADR-0002: Spring Data JPA / Hibernate; no hand-written JDBC.
- ADR-0003: canonical product separated from seller listing.
- ADR-0004: AI can only call allow-listed backend tools; no direct database access.
