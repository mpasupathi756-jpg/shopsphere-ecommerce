package com.shopsphere.identity.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="users")
public class UserAccount {
 @Id @UuidGenerator private UUID id;
 @Column(nullable=false,unique=true,length=254) private String email;
 @Column(name="password_hash",nullable=false,length=100) private String passwordHash;
 @Column(name="display_name",nullable=false,length=120) private String displayName;
 @Column(name="account_status",nullable=false,length=24) private String accountStatus="ACTIVE";
 @Column(name="email_verified",nullable=false) private boolean emailVerified;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @ManyToMany(fetch=FetchType.EAGER) @JoinTable(name="user_roles",joinColumns=@JoinColumn(name="user_id"),inverseJoinColumns=@JoinColumn(name="role_id")) private Set<Role> roles=new HashSet<>();
 protected UserAccount() {}
 public UserAccount(String email,String passwordHash,String displayName,Role role){this.email=email;this.passwordHash=passwordHash;this.displayName=displayName;this.roles.add(role);}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public String getDisplayName(){return displayName;} public String getAccountStatus(){return accountStatus;} public boolean isEmailVerified(){return emailVerified;} public Instant getCreatedAt(){return createdAt;} public Set<Role> getRoles(){return Set.copyOf(roles);} public boolean isActive(){return "ACTIVE".equals(accountStatus);} public void assignRole(Role role){this.roles.add(role);}
}
