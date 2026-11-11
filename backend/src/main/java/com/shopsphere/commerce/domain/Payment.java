package com.shopsphere.commerce.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="payments")
public class Payment {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id",nullable=false) private CustomerOrder order;
 @Column(nullable=false,length=40) private String provider="UNCONFIGURED";
 @Column(name="provider_reference",length=180) private String providerReference;
 @Column(nullable=false,length=24) private String status="NOT_CONFIGURED";
 @Column(name="amount_minor",nullable=false) private long amountMinor;
 @Column(nullable=false,length=3) private String currency="INR";
 @Column(name="idempotency_key",nullable=false,unique=true,length=100) private String idempotencyKey;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected Payment(){}
 public Payment(CustomerOrder order,long amount,String currency,String key){this.order=order;this.amountMinor=amount;this.currency=currency;this.idempotencyKey=key;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public CustomerOrder getOrder(){return order;} public String getProvider(){return provider;} public String getProviderReference(){return providerReference;} public String getIdempotencyKey(){return idempotencyKey;} public String getStatus(){return status;} public long getAmountMinor(){return amountMinor;} public String getCurrency(){return currency;} public void markPending(String providerName,String reference){provider=providerName;providerReference=reference;status="PENDING";} public void markCaptured(){if(!"PENDING".equals(status))throw new IllegalStateException("Payment is not pending");status="CAPTURED";} public void markFailed(){if(!"PENDING".equals(status))throw new IllegalStateException("Payment is not pending");status="FAILED";} public void cancel(){if("PENDING".equals(status))status="CANCELLED";}
}
