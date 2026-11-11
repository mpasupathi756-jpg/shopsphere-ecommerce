# Identity API security notes

- Registration always assigns CUSTOMER. A public request cannot choose SELLER or ADMIN.
- Passwords are BCrypt-hashed; raw passwords are never persisted or logged.
- Access JWTs are short-lived and signed with an environment-provided HMAC key.
- Refresh tokens are random, stored only as SHA-256 hashes, held in HttpOnly cookies and rotated on every refresh.
- Refresh/logout/register/login require a double-submit CSRF header and cookie.
- In production set REFRESH_COOKIE_SECURE=true, configure a strong random JWT_SECRET_BASE64 (at least 32 bytes), HTTPS and an exact FRONTEND_ORIGIN.
- Email verification and seller KYC are later workflows; email_verified remains false until a real verification process is implemented.
- Do not expose refresh-token values to JavaScript or localStorage.
