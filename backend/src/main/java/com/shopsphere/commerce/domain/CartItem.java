package com.shopsphere.commerce.domain;
import com.shopsphere.marketplace.domain.Listing;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="cart_items",uniqueConstraints=@UniqueConstraint(columnNames={"cart_id","listing_id"}))
public class CartItem {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="cart_id",nullable=false) private Cart cart;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="listing_id",nullable=false) private Listing listing;
 @Column(nullable=false) private int quantity;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected CartItem(){}
 public CartItem(Cart cart,Listing listing,int quantity){this.cart=cart;this.listing=listing;this.quantity=quantity;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public Cart getCart(){return cart;} public Listing getListing(){return listing;} public int getQuantity(){return quantity;} public void setQuantity(int value){quantity=value;}
}
