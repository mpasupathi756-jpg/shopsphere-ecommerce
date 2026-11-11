package com.shopsphere.commerce.domain;
import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.Duration;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="orders",uniqueConstraints=@UniqueConstraint(columnNames={"order_number"}))
public class CustomerOrder {
 @Id @UuidGenerator private UUID id;
 @Column(name="order_number",nullable=false,unique=true,length=32) private String orderNumber;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="customer_id",nullable=false) private UserAccount customer;
 @Column(name="idempotency_key",length=80) private String idempotencyKey;
 @Column(nullable=false,length=24) private String status="PAYMENT_PENDING";
 @Column(nullable=false,length=3) private String currency="INR";
 @Column(name="subtotal_minor",nullable=false) private long subtotalMinor;
 @Column(name="shipping_minor",nullable=false) private long shippingMinor;
 @Column(name="discount_minor",nullable=false) private long discountMinor;
 @Column(name="total_minor",nullable=false) private long totalMinor;
 @Column(name="shipping_recipient",nullable=false,length=120) private String shippingRecipient;
 @Column(name="shipping_phone",nullable=false,length=24) private String shippingPhone;
 @Column(name="shipping_line1",nullable=false,length=180) private String shippingLine1;
 @Column(name="shipping_line2",length=180) private String shippingLine2;
 @Column(name="shipping_city",nullable=false,length=100) private String shippingCity;
 @Column(name="shipping_state",nullable=false,length=100) private String shippingState;
 @Column(name="shipping_postal_code",nullable=false,length=12) private String shippingPostalCode;
 @Column(name="shipping_country",nullable=false,length=80) private String shippingCountry;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Column(name="expires_at") private Instant expiresAt;
 protected CustomerOrder(){}
 public CustomerOrder(String number,UserAccount customer,String key,long subtotal,long shipping,long discount,CustomerAddress address){this.orderNumber=number;this.customer=customer;this.idempotencyKey=key;this.subtotalMinor=subtotal;this.shippingMinor=shipping;this.discountMinor=discount;this.totalMinor=Math.addExact(Math.subtractExact(Math.addExact(subtotal,shipping),discount),0);this.shippingRecipient=address.getRecipientName();this.shippingPhone=address.getPhone();this.shippingLine1=address.getAddressLine1();this.shippingLine2=address.getAddressLine2();this.shippingCity=address.getCity();this.shippingState=address.getState();this.shippingPostalCode=address.getPostalCode();this.shippingCountry=address.getCountry();this.expiresAt=Instant.now().plus(Duration.ofMinutes(15));}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public String getOrderNumber(){return orderNumber;} public UserAccount getCustomer(){return customer;} public String getIdempotencyKey(){return idempotencyKey;} public String getStatus(){return status;} public String getCurrency(){return currency;} public long getSubtotalMinor(){return subtotalMinor;} public long getShippingMinor(){return shippingMinor;} public long getDiscountMinor(){return discountMinor;} public long getTotalMinor(){return totalMinor;} public Instant getCreatedAt(){return createdAt;} public Instant getExpiresAt(){return expiresAt;} public String getShippingRecipient(){return shippingRecipient;} public String getShippingPhone(){return shippingPhone;} public String getShippingLine1(){return shippingLine1;} public String getShippingLine2(){return shippingLine2;} public String getShippingCity(){return shippingCity;} public String getShippingState(){return shippingState;} public String getShippingPostalCode(){return shippingPostalCode;} public String getShippingCountry(){return shippingCountry;} public void markPaid(){if(!"PAYMENT_PENDING".equals(status))throw new IllegalStateException("Order is not awaiting payment");status="PAID";} public void markProcessing(){if(!"PAID".equals(status))throw new IllegalStateException("Order must be paid before processing");status="PROCESSING";} public void markShipped(){if(!"PROCESSING".equals(status))throw new IllegalStateException("Order must be processing before shipment");status="SHIPPED";} public void markDelivered(){if(!"SHIPPED".equals(status))throw new IllegalStateException("Order must be shipped before delivery");status="DELIVERED";} public void cancel(){status="CANCELLED";} public boolean isExpired(Instant now){return expiresAt!=null&&expiresAt.isBefore(now);}
}
