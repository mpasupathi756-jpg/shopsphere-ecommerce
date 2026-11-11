package com.shopsphere.identity.api;

import com.shopsphere.identity.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final String REFRESH_COOKIE = "shopsphere_refresh";
    private static final String CSRF_COOKIE = "shopsphere_csrf";
    private final AuthService auth;
    private final boolean secureCookie;
    private final SecureRandom random = new SecureRandom();

    public AuthController(AuthService auth, @Value("${app.security.refresh.secure-cookie:false}") boolean secureCookie) {
        this.auth = auth; this.secureCookie = secureCookie;
    }

    @GetMapping("/csrf")
    public Map<String,String> csrf(HttpServletResponse response) {
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(CSRF_COOKIE, token, false, 3600).toString());
        return Map.of("csrfToken", token);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
            @RequestHeader("X-CSRF-TOKEN") String csrfHeader, @CookieValue(CSRF_COOKIE) String csrfCookie,
            HttpServletResponse response) {
        validateCsrf(csrfHeader, csrfCookie);
        AuthService.Session session = auth.register(request);
        setRefreshCookie(response, session.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(session.response());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request,
            @RequestHeader("X-CSRF-TOKEN") String csrfHeader, @CookieValue(CSRF_COOKIE) String csrfCookie,
            HttpServletResponse response) {
        validateCsrf(csrfHeader, csrfCookie);
        AuthService.Session session = auth.login(request);
        setRefreshCookie(response, session.refreshToken());
        return session.response();
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue(value=REFRESH_COOKIE, required=false) String refreshToken,
            @RequestHeader("X-CSRF-TOKEN") String csrfHeader, @CookieValue(CSRF_COOKIE) String csrfCookie,
            HttpServletResponse response) {
        validateCsrf(csrfHeader, csrfCookie);
        AuthService.Session session = auth.refresh(refreshToken);
        setRefreshCookie(response, session.refreshToken());
        return session.response();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value=REFRESH_COOKIE, required=false) String refreshToken,
            @RequestHeader("X-CSRF-TOKEN") String csrfHeader, @CookieValue(CSRF_COOKIE) String csrfCookie,
            HttpServletResponse response) {
        validateCsrf(csrfHeader, csrfCookie);
        auth.logout(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(REFRESH_COOKIE, "", true, 0).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(CSRF_COOKIE, "", false, 0).toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return auth.currentUser(UUID.fromString(jwt.getSubject()));
    }

    private void validateCsrf(String header, String cookie) {
        if (header == null || cookie == null || !MessageDigest.isEqual(header.getBytes(java.nio.charset.StandardCharsets.UTF_8), cookie.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "CSRF validation failed");
        }
    }
    private void setRefreshCookie(HttpServletResponse response, String token) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(REFRESH_COOKIE, token, true, refreshTtlDays * 24 * 60 * 60).toString());
    }
    private ResponseCookie cookie(String name, String value, boolean httpOnly, long maxAgeSeconds) {
        return ResponseCookie.from(name, value).httpOnly(httpOnly).secure(secureCookie)
                .sameSite(secureCookie ? "None" : "Lax").path("/api/v1/auth")
                .maxAge(java.time.Duration.ofSeconds(maxAgeSeconds)).build();
    }
}
