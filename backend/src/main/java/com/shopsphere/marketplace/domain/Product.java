package com.shopsphere.marketplace.domain;
import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="products")
public class Product {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="category_id",nullable=false) private Category category;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="brand_id") private Brand brand;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="created_by_user_id",nullable=false) private UserAccount createdBy;
 @Column(nullable=false,length=180) private String title;
 @Column(nullable=false,unique=true,length=200) private String slug;
 @Column(nullable=false,columnDefinition="text") private String description;
 @Column(name="product_status",nullable=false,length=24) private String productStatus="PENDING_REVIEW";
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected Product(){}
 public Product(Category category,Brand brand,UserAccount createdBy,String title,String slug,String description){this.category=category;this.brand=brand;this.createdBy=createdBy;this.title=title;this.slug=slug;this.description=description;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public Category getCategory(){return category;} public Brand getBrand(){return brand;} public UserAccount getCreatedBy(){return createdBy;} public String getTitle(){return title;} public String getSlug(){return slug;} public String getDescription(){return description;} public String getProductStatus(){return productStatus;} public void approve(){productStatus="ACTIVE";} public void reject(){productStatus="REJECTED";}
}
