# ShopSphere

Premium, AI-first multi-seller marketplace built as a modular monolith.

## Current delivery status

- **Merged to main:** project foundation, customer identity, seller onboarding, product catalogue, seller listings, inventory, product discovery, cart, wishlist, product comparison and read-only multi-seller checkout preview.
- **Merged to main:** idempotent order creation, seller-specific order splits, inventory reservation and expiry, local mock payment, order history and seller fulfilment.
- **Merged to main (Phase 5):** authenticated Spring AI shopping assistant with local Ollama model integration, read-only catalogue/order tools and a React chat page. Local Ollama runtime/tool-calling verification on the developer computer is still pending.
- **Phase 6 implemented:** verified-purchase product reviews and ratings, V6 schema, backend APIs, published-rating aggregates, product review UI, customer review management, admin report queue and integration tests. GitHub Actions run 43017417327 passed frontend typecheck/build, backend Maven verification and PostgreSQL 16 Flyway/Hibernate schema validation. PR #8 is ready for merge.
- **Production payments:** disabled until a marketplace-capable provider and seller settlement model are selected and integrated.
- **AI model provider:** Ollama is the local development provider (Qwen 2.5 3B default). Production AI hosting is undecided; no external model API key is configured.

## Technology stack

- Frontend: React, TypeScript, Vite, Tailwind CSS, TanStack Query.
- Backend: Java 21, Spring Boot 3.5, Spring MVC, Spring Security.
- Persistence: Spring Data JPA / Hibernate; no hand-written JDBC.
- Database: PostgreSQL 16+ with Flyway migrations.
- Testing: JUnit 5, Spring Boot Test, MockMvc, H2 integration tests, PostgreSQL migration/schema smoke test, frontend TypeScript checks and production build.
- Target hosting: Vercel (frontend), Render (API), Supabase (PostgreSQL and object storage). No production infrastructure has been provisioned.

## Repository layout

- frontend/ — React application.
- backend/ — Spring Boot REST API.
- docs/architecture/ — architecture and implementation decisions.

## Prerequisites

JDK 21, Maven 3.9+, Node.js 22+ and PostgreSQL 16+.

## Local setup

1. Create a PostgreSQL database named shopsphere.
2. Copy .env.example to your local environment configuration and set DB_URL, DB_USERNAME, DB_PASSWORD, FRONTEND_ORIGIN and a fresh JWT_SECRET_BASE64.
3. Generate a JWT key with at least 32 random bytes. PowerShell:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

4. Start the backend:

```bash
cd backend
mvn spring-boot:run
```

5. Start the frontend in a second terminal:

```bash
cd frontend
npm install
npm run dev
```

- Frontend: http://localhost:5173
- API health: http://localhost:8080/actuator/health
- API info: http://localhost:8080/api/v1/system/info
- OpenAPI UI: http://localhost:8080/swagger-ui.html

All schema changes must use Flyway migrations. Never commit real credentials.

## Local payment safety

The default local profile enables a mock payment flow for development and automated tests. It never charges real money. Do not deploy with the local profile.

Non-local profiles use an unconfigured payment gateway that rejects order creation. Production must explicitly set SPRING_PROFILES_ACTIVE=prod, configure a supported Indian marketplace payment provider, seller settlement, signed webhooks, refunds and reconciliation, and set REFRESH_COOKIE_SECURE=true. Until these are implemented and reviewed, real customer payments must remain disabled.

Pending mock payment orders expire after 15 minutes by default and release their stock reservations. APP_ORDERS_EXPIRY_SCAN_MS controls the expiry scan interval.

## AI assistant safety

The Phase 5 assistant uses the local Ollama service in development. It exposes bounded, read-only catalogue and authenticated customer-order tools. The model has no SQL/repository access, arbitrary URL fetching, cart/order writes, payments, cancellation, seller or admin actions. Do not expose a developer's localhost Ollama service publicly. Production model hosting, rate limiting, persistent chat history and privacy/retention controls remain open production work.

## CI

GitHub Actions runs frontend typecheck and production build, backend Maven verification, and a PostgreSQL 16 startup smoke test that executes Flyway migrations and Hibernate schema validation.
