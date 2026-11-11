package com.shopsphere.commerce.domain;
import com.shopsphere.marketplace.domain.Product;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="wishlist_items",uniqueConstraints=@UniqueConstraint(columnNames={"wishlist_id","product_id"}))
public class WishlistItem {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="wishlist_id",nullable=false) private Wishlist wishlist;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected WishlistItem(){}
 public WishlistItem(Wishlist wishlist,Product product){this.wishlist=wishlist;this.product=product;this.createdAt=Instant.now();}
 public UUID getId(){return id;} public Product getProduct(){return product;}
}
