package com.shopsphere.identity.service;
import com.shopsphere.identity.api.AuthResponse;
import com.shopsphere.identity.api.LoginRequest;
import com.shopsphere.identity.api.RegisterRequest;
import com.shopsphere.identity.api.UserProfileResponse;
import com.shopsphere.identity.domain.Role;
import com.shopsphere.identity.domain.UserAccount;
import com.shopsphere.identity.repository.RoleRepository;
import com.shopsphere.identity.repository.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class AuthService {
 private final UserAccountRepository users; private final RoleRepository roles; private final PasswordEncoder passwords; private final JwtTokenService jwt; private final RefreshTokenService refresh;
 public AuthService(UserAccountRepository users,RoleRepository roles,PasswordEncoder passwords,JwtTokenService jwt,RefreshTokenService refresh){this.users=users;this.roles=roles;this.passwords=passwords;this.jwt=jwt;this.refresh=refresh;}
 @Transactional public Session register(RegisterRequest request){String email=normalize(request.email());if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists");if(request.password().getBytes(StandardCharsets.UTF_8).length>72)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password must be no more than 72 UTF-8 bytes");Role customer=roles.findByCode("CUSTOMER").orElseThrow(()->new IllegalStateException("CUSTOMER role is missing; run database migrations"));UserAccount user=users.save(new UserAccount(email,passwords.encode(request.password()),request.displayName().trim(),customer));return createSession(user);}
 @Transactional public Session login(LoginRequest request){UserAccount user=users.findByEmailIgnoreCase(normalize(request.email())).orElseThrow(this::invalidCredentials);if(!user.isActive()||!passwords.matches(request.password(),user.getPasswordHash()))throw invalidCredentials();return createSession(user);}
 @Transactional public Session refresh(String raw){UserAccount user=refresh.rotate(raw);return new Session(new AuthResponse(jwt.createAccessToken(user),"Bearer",jwt.expiresInSeconds(),profile(user)),refresh.issue(user));}
 @Transactional public void logout(String raw){refresh.revoke(raw);}
 @Transactional(readOnly=true) public UserProfileResponse currentUser(java.util.UUID id){UserAccount user=users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found"));return profile(user);}
 private Session createSession(UserAccount user){return new Session(new AuthResponse(jwt.createAccessToken(user),"Bearer",jwt.expiresInSeconds(),profile(user)),refresh.issue(user));}
 private UserProfileResponse profile(UserAccount user){return new UserProfileResponse(user.getId(),user.getEmail(),user.getDisplayName(),user.getRoles().stream().map(Role::getCode).sorted().toList(),user.isEmailVerified());}
 private String normalize(String email){return email.trim().toLowerCase(java.util.Locale.ROOT);}
 private ResponseStatusException invalidCredentials(){return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect");}
 public record Session(AuthResponse response,String refreshToken){}
}
