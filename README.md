# ShopSphere

Premium AI-first multi-seller marketplace — Phase 0 foundation.

## Stack
- Frontend: React, TypeScript, Vite and Tailwind CSS
- Backend: Java 21, Spring Boot, Spring MVC, Spring Security
- Persistence: Spring Data JPA / Hibernate (no hand-written JDBC)
- Database: PostgreSQL with Flyway migrations
- Target: Vercel (frontend), Render (API), Supabase (database and object storage)

## Repository layout
- `frontend/` — React application
- `backend/` — Spring Boot API
- `docs/architecture/` — architecture decisions

## Prerequisites
JDK 21, Maven 3.9+, Node.js 22+ and PostgreSQL 16+.

## Run locally
Create a PostgreSQL database named `shopsphere`, then configure DB_URL, DB_USERNAME, DB_PASSWORD and FRONTEND_ORIGIN locally.

Backend: `cd backend && mvn spring-boot:run`
Frontend: `cd frontend && npm install && npm run dev`

Health: http://localhost:8080/actuator/health
API info: http://localhost:8080/api/v1/system/info
Frontend: http://localhost:5173

All schema changes must use Flyway migrations. Never commit credentials. The current security configuration is scaffolding only and is not production-ready.

## Milestone
Phase 0 establishes the project skeleton, customer-facing UI shell, API health/info endpoints, database migration baseline and CI. Authentication, seller workflows, catalogue, checkout and AI are later milestones.
## Local payment and security notes
- Copy `.env.example` to a local environment file and set a fresh `JWT_SECRET_BASE64` (at least 32 random bytes, Base64 encoded). Never reuse the example placeholder or commit a real secret.
- Generate a secret on PowerShell: `[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))`.
- The default Spring profile is `local`; it enables a mock payment flow that never charges real money. Do not deploy with the local profile.
- Production must explicitly set `SPRING_PROFILES_ACTIVE=prod`, configure a supported marketplace payment adapter and secrets, and set `REFRESH_COOKIE_SECURE=true`. Until a provider is selected and integrated, checkout is disabled outside local development.
- Payment reservations expire after 15 minutes by default; tune `APP_ORDERS_EXPIRY_SCAN_MS` only after reviewing operational requirements.
