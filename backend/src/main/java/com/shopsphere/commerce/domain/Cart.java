package com.shopsphere.commerce.domain;
import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="carts")
public class Cart {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false,unique=true) private UserAccount user;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected Cart(){}
 public Cart(UserAccount user){this.user=user;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public UserAccount getUser(){return user;}
}
