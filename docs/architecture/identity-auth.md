# Identity and session design — Phase 1

## Public API

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /api/v1/auth/csrf | Issue a double-submit CSRF token cookie and response token |
| POST | /api/v1/auth/register | Create a customer account; role is assigned server-side |
| POST | /api/v1/auth/login | Verify email/password and issue a short-lived access JWT |
| POST | /api/v1/auth/refresh | Rotate the HttpOnly refresh cookie and issue a new access JWT |
| POST | /api/v1/auth/logout | Revoke the presented refresh token and clear cookies |
| GET | /api/v1/auth/me | Return the authenticated account profile |

## Session lifecycle

1. Browser requests the CSRF endpoint with credentials enabled.
2. Server sets a non-HttpOnly CSRF cookie and returns the same random token in JSON.
3. Browser sends the token in \`X-CSRF-TOKEN\` for state-changing auth requests.
4. Register/login returns a short-lived signed access token in the response and a random refresh token in an HttpOnly cookie.
5. Access token remains in frontend memory only; it is not stored in localStorage or sessionStorage.
6. Page reload or access-token expiry uses the refresh cookie. Server stores only a SHA-256 hash, locks and revokes the old token, then issues a new one.
7. Logout revokes the refresh token and clears both cookies.

## Authorization

- Registration always assigns CUSTOMER.
- ADMIN and SELLER roles cannot be requested in the public registration payload.
- \`/api/v1/admin/**\` requires ADMIN; \`/api/v1/seller/**\` requires SELLER; other application APIs require a valid JWT unless explicitly public.
- JWT roles are converted to Spring Security \`ROLE_*\` authorities.

## Database changes

- V1 identity tables: users, roles and user_roles.
- V2 refresh_tokens: hashed token, expiry, revocation timestamp and user foreign key; partial index for active user sessions.
- Email uniqueness is enforced case-insensitively by a unique lower(email) index.

## Production requirements and current gaps

- Set a random JWT_SECRET_BASE64 with at least 32 bytes, HTTPS, exact FRONTEND_ORIGIN and REFRESH_COOKIE_SECURE=true.
- Configure a production secret manager; never use the repository's local development fallback secret in production.
- Email verification, password reset, login rate limiting, suspicious-session alerts and seller KYC are not yet implemented.
- Customer accounts are active immediately for the development milestone; email_verified remains false. Add verified-email gating before public production launch.
- The current double-submit CSRF check protects the cookie-backed refresh/logout flow; keep CORS restricted to known frontend origins.
- Do not deploy until production secret, email verification, rate limiting and account recovery controls are completed.
