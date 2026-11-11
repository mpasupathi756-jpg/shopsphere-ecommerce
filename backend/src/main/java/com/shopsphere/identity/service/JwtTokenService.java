package com.shopsphere.identity.service;
import com.shopsphere.identity.domain.UserAccount;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
@Service
public class JwtTokenService {
 private final JwtEncoder encoder; private final String issuer; private final long ttlMinutes;
 public JwtTokenService(JwtEncoder encoder,@Value("${app.security.jwt.issuer}") String issuer,@Value("${app.security.jwt.access-ttl-minutes:15}") long ttlMinutes){this.encoder=encoder;this.issuer=issuer;this.ttlMinutes=ttlMinutes;}
 public String createAccessToken(UserAccount user){Instant now=Instant.now();List<String> roles=user.getRoles().stream().map(r->r.getCode()).sorted().toList();JwtClaimsSet claims=JwtClaimsSet.builder().issuer(issuer).issuedAt(now).expiresAt(now.plusSeconds(ttlMinutes*60)).subject(user.getId().toString()).claim("email",user.getEmail()).claim("name",user.getDisplayName()).claim("roles",roles).build();JwsHeader header=JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();return encoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue();}
 public long expiresInSeconds(){return ttlMinutes*60;}
}
