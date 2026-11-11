# ShopSphere

Premium, AI-first multi-seller marketplace built as a modular monolith.

## Current delivery status

- **Merged to main:** project foundation, customer identity, seller onboarding, product catalogue, seller listings, inventory, product discovery, cart, wishlist, product comparison and read-only multi-seller checkout preview.
- **Merged to main:** idempotent order creation, seller-specific order splits, inventory reservation and expiry, local mock payment, order history and seller fulfilment.
- **Phase 5 merged to main:** authenticated Spring AI shopping assistant with local Ollama/Qwen 2.5 3B, read-only catalogue/order tools and a React chat page. Local Ollama end-to-end runtime verification remains pending.
- **Production payments:** disabled until a marketplace-capable provider and seller settlement model are selected and integrated.
- **AI model provider:** local Ollama is the development default; no external model API is configured. Production model hosting remains undecided.

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

## Local AI assistant (Phase 5)

1. Install Ollama from https://ollama.com/download and start the Ollama service.
2. Pull the configured model: `ollama pull qwen2.5:3b`.
3. Confirm the model is available: `ollama list` and verify `qwen2.5:3b` appears.
4. Start PostgreSQL, backend and frontend using the local setup above.
5. Sign in as a customer and open http://localhost:5173/assistant.
6. Test catalogue search, comparison of 2–4 products, own order list and own order detail. Also test the chat endpoint while signed out (must reject) and verify one customer cannot retrieve another customer's orders.
7. Keep Ollama bound to localhost; do not expose port 11434 publicly.

The assistant endpoint uses `OLLAMA_BASE_URL` (default `http://localhost:11434`) and `OLLAMA_MODEL` (default `qwen2.5:3b`). The model is not downloaded automatically by Spring Boot. Phase 5 is not end-to-end verified until these local runtime and authorization checks pass.

## Local payment safety

The default local profile enables a mock payment flow for development and automated tests. It never charges real money. Do not deploy with the local profile.

Non-local profiles use an unconfigured payment gateway that rejects order creation. Production must explicitly set SPRING_PROFILES_ACTIVE=prod, configure a supported Indian marketplace payment provider, seller settlement, signed webhooks, refunds and reconciliation, and set REFRESH_COOKIE_SECURE=true. Until these are implemented and reviewed, real customer payments must remain disabled.

Pending mock payment orders expire after 15 minutes by default and release their stock reservations. APP_ORDERS_EXPIRY_SCAN_MS controls the expiry scan interval.

## CI

GitHub Actions runs frontend typecheck and production build, backend Maven verification, and a PostgreSQL 16 startup smoke test that executes Flyway migrations and Hibernate schema validation.
