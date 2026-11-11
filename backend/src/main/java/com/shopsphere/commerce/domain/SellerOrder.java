package com.shopsphere.commerce.domain;
import com.shopsphere.marketplace.domain.Seller;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="seller_orders")
public class SellerOrder {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id",nullable=false) private CustomerOrder order;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="seller_id",nullable=false) private Seller seller;
 @Column(nullable=false,length=24) private String status="PAYMENT_PENDING";
 @Column(name="subtotal_minor",nullable=false) private long subtotalMinor;
 @Column(name="shipping_minor",nullable=false) private long shippingMinor;
 @Column(name="total_minor",nullable=false) private long totalMinor;
 @Column(name="tracking_number",length=100) private String trackingNumber;
 @Column(length=100) private String carrier;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected SellerOrder(){}
 public SellerOrder(CustomerOrder order,Seller seller,long subtotal,long shipping){this.order=order;this.seller=seller;this.subtotalMinor=subtotal;this.shippingMinor=shipping;this.totalMinor=Math.addExact(subtotal,shipping);}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public CustomerOrder getOrder(){return order;} public Seller getSeller(){return seller;} public String getStatus(){return status;} public long getSubtotalMinor(){return subtotalMinor;} public long getShippingMinor(){return shippingMinor;} public long getTotalMinor(){return totalMinor;} public void cancel(){status="CANCELLED";}
}
