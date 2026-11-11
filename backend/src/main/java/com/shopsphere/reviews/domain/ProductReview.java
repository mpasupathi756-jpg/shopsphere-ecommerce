package com.shopsphere.reviews.domain;

import com.shopsphere.commerce.domain.OrderItem;
import com.shopsphere.identity.domain.UserAccount;
import com.shopsphere.marketplace.domain.Product;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name="product_reviews", uniqueConstraints=@UniqueConstraint(name="ux_product_reviews_customer_product", columnNames={"customer_id","product_id"}))
public class ProductReview {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="customer_id",nullable=false) private UserAccount customer;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_item_id",nullable=false) private OrderItem orderItem;
 @Column(nullable=false) private short rating;
 @Column(nullable=false,length=120) private String title;
 @Column(nullable=false,length=3000) private String body;
 @Column(nullable=false,length=16) private String status="PUBLISHED";
 @Column(name="moderation_reason",length=500) private String moderationReason;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="moderated_by") private UserAccount moderatedBy;
 @Column(name="moderated_at") private Instant moderatedAt;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected ProductReview(){}
 public ProductReview(Product product,UserAccount customer,OrderItem orderItem,int rating,String title,String body){this.product=product;this.customer=customer;this.orderItem=orderItem;this.rating=(short)rating;this.title=title;this.body=body;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;}
 @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public void edit(int rating,String title,String body){if(!"PUBLISHED".equals(status))throw new IllegalStateException("Only published reviews can be edited");this.rating=(short)rating;this.title=title;this.body=body;}
 public void moderate(String action,String reason,UserAccount actor){if(!java.util.Set.of("PUBLISHED","HIDDEN","REMOVED").contains(action))throw new IllegalArgumentException("Invalid moderation action");status=action;moderationReason=reason;moderatedBy=actor;moderatedAt=Instant.now();}
 public UUID getId(){return id;} public Product getProduct(){return product;} public UserAccount getCustomer(){return customer;} public OrderItem getOrderItem(){return orderItem;} public int getRating(){return rating;} public String getTitle(){return title;} public String getBody(){return body;} public String getStatus(){return status;} public String getModerationReason(){return moderationReason;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}