package com.shopsphere.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
    @Bean SecretKey jwtSecretKey(@Value("${app.security.jwt.secret-base64}") String secret) {
        byte[] bytes = java.util.Base64.getDecoder().decode(secret);
        if (bytes.length < 32) throw new IllegalStateException("JWT secret must contain at least 256 bits");
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
    @Bean JwtEncoder jwtEncoder(SecretKey key) { return new NimbusJwtEncoder(new ImmutableSecret<>(key)); }
    @Bean JwtDecoder jwtDecoder(SecretKey key, @Value("${app.security.jwt.issuer}") String issuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles"); roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, Converter<Jwt, ? extends AbstractAuthenticationToken> jwtConverter) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable).cors(c -> {})
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/actuator/health","/actuator/info","/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .requestMatchers("/api/v1/auth/register","/api/v1/auth/login","/api/v1/auth/refresh","/api/v1/auth/csrf").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/v1/system/info").permitAll()
                .requestMatchers("/api/v1/catalog/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/seller/**").hasRole("SELLER")
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtConverter)))
            .build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.frontend-origin:http://localhost:5173}") String origins) {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(Arrays.asList(origins.split(",")));
        c.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Idempotency-Key","X-CSRF-TOKEN"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",c); return source;
    }
}
