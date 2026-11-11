package com.shopsphere.identity.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="refresh_tokens")
public class RefreshToken {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private UserAccount user;
 @Column(name="token_hash",nullable=false,unique=true,length=64) private String tokenHash;
 @Column(name="expires_at",nullable=false) private Instant expiresAt;
 @Column(name="revoked_at") private Instant revokedAt;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected RefreshToken() {}
 public RefreshToken(UserAccount user,String tokenHash,Instant expiresAt){this.user=user;this.tokenHash=tokenHash;this.expiresAt=expiresAt;this.createdAt=Instant.now();}
 public UserAccount getUser(){return user;} public String getTokenHash(){return tokenHash;} public Instant getExpiresAt(){return expiresAt;} public Instant getRevokedAt(){return revokedAt;} public boolean isUsable(Instant now){return revokedAt==null&&expiresAt.isAfter(now);} public void revoke(Instant now){revokedAt=now;}
}
