package com.shopsphere.marketplace.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="brands")
public class Brand {
 @Id @UuidGenerator private UUID id;
 @Column(nullable=false,unique=true,length=120) private String name;
 @Column(nullable=false,unique=true,length=140) private String slug;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected Brand(){}
 public UUID getId(){return id;} public String getName(){return name;} public String getSlug(){return slug;}
}
