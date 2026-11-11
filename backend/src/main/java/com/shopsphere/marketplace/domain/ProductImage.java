package com.shopsphere.marketplace.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="product_images")
public class ProductImage {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @Column(name="storage_key",nullable=false,length=500) private String storageKey;
 @Column(name="alt_text",nullable=false,length=180) private String altText;
 @Column(name="sort_order",nullable=false) private int sortOrder;
 @Column(name="is_primary",nullable=false) private boolean primary;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected ProductImage(){}
 public UUID getId(){return id;} public String getStorageKey(){return storageKey;} public String getAltText(){return altText;} public int getSortOrder(){return sortOrder;} public boolean isPrimary(){return primary;}
}
