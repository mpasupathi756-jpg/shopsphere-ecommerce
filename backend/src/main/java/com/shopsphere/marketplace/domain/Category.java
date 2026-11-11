package com.shopsphere.marketplace.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="categories")
public class Category {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="parent_id") private Category parent;
 @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,unique=true,length=120) private String slug;
 @Column(length=500) private String description;
 @Column(nullable=false,length=16) private String status="ACTIVE";
 @Column(name="display_order",nullable=false) private int displayOrder;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected Category(){}
 public UUID getId(){return id;} public String getName(){return name;} public String getSlug(){return slug;} public String getDescription(){return description;} public String getStatus(){return status;} public int getDisplayOrder(){return displayOrder;}
}
