package com.shopsphere.identity.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="roles")
public class Role {
 @Id @UuidGenerator private UUID id;
 @Column(nullable=false,unique=true,length=40) private String code;
 @Column(length=160) private String description;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected Role() {}
 public UUID getId(){return id;} public String getCode(){return code;} public String getDescription(){return description;} public Instant getCreatedAt(){return createdAt;}
}
