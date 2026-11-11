package com.shopsphere.identity.service;
import com.shopsphere.identity.domain.RefreshToken;
import com.shopsphere.identity.domain.UserAccount;
import com.shopsphere.identity.repository.RefreshTokenRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class RefreshTokenService {
 private final RefreshTokenRepository repository; private final long ttlDays; private final SecureRandom random=new SecureRandom();
 public RefreshTokenService(RefreshTokenRepository repository,@Value("${app.security.refresh.ttl-days:14}") long ttlDays){this.repository=repository;this.ttlDays=ttlDays;}
 @Transactional public String issue(UserAccount user){String raw=generate();repository.save(new RefreshToken(user,hash(raw),Instant.now().plus(ttlDays,ChronoUnit.DAYS)));return raw;}
 @Transactional public UserAccount rotate(String raw){if(raw==null||raw.isBlank())throw unauthorized();RefreshToken current=repository.findByHashForUpdate(hash(raw)).orElseThrow(this::unauthorized);Instant now=Instant.now();if(!current.isUsable(now)||!current.getUser().isActive())throw unauthorized();current.revoke(now);repository.flush();return current.getUser();}
 @Transactional public void revoke(String raw){if(raw==null||raw.isBlank())return;repository.findByHashForUpdate(hash(raw)).ifPresent(token->token.revoke(Instant.now()));}
 private String generate(){byte[] bytes=new byte[48];random.nextBytes(bytes);return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);}
 private String hash(String raw){try{return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException("SHA-256 is unavailable",e);}}
 private ResponseStatusException unauthorized(){return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Session is invalid or expired");}
}
