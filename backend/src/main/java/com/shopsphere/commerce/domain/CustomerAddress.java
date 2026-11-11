package com.shopsphere.commerce.domain;
import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="customer_addresses")
public class CustomerAddress {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private UserAccount user;
 @Column(name="recipient_name",nullable=false,length=120) private String recipientName;
 @Column(nullable=false,length=24) private String phone;
 @Column(name="address_line1",nullable=false,length=180) private String addressLine1;
 @Column(name="address_line2",length=180) private String addressLine2;
 @Column(nullable=false,length=100) private String city;
 @Column(nullable=false,length=100) private String state;
 @Column(name="postal_code",nullable=false,length=12) private String postalCode;
 @Column(nullable=false,length=80) private String country="India";
 @Column(name="is_default",nullable=false) private boolean defaultAddress;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected CustomerAddress(){}
 public CustomerAddress(UserAccount user,String recipientName,String phone,String line1,String line2,String city,String state,String postalCode,String country,boolean defaultAddress){this.user=user;this.recipientName=recipientName;this.phone=phone;this.addressLine1=line1;this.addressLine2=line2;this.city=city;this.state=state;this.postalCode=postalCode;this.country=country;this.defaultAddress=defaultAddress;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public UserAccount getUser(){return user;} public String getRecipientName(){return recipientName;} public String getPhone(){return phone;} public String getAddressLine1(){return addressLine1;} public String getAddressLine2(){return addressLine2;} public String getCity(){return city;} public String getState(){return state;} public String getPostalCode(){return postalCode;} public String getCountry(){return country;} public boolean isDefaultAddress(){return defaultAddress;} public void setDefaultAddress(boolean value){defaultAddress=value;}
}
