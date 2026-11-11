package com.shopsphere.commerce.domain;
import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="wishlists")
public class Wishlist {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false,unique=true) private UserAccount user;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected Wishlist(){}
 public Wishlist(UserAccount user){this.user=user;this.createdAt=Instant.now();}
 public UUID getId(){return id;} public UserAccount getUser(){return user;}
}
