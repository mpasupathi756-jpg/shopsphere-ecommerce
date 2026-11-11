package com.shopsphere.marketplace.domain;
import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="sellers")
public class Seller {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false,unique=true) private UserAccount user;
 @Column(name="legal_name",nullable=false,length=180) private String legalName;
 @Column(name="store_name",nullable=false,length=120) private String storeName;
 @Column(name="business_email",nullable=false,length=254) private String businessEmail;
 @Column(nullable=false,length=24) private String phone;
 @Column(name="business_address",nullable=false,length=500) private String businessAddress;
 @Column(length=15) private String gstin;
 @Column(nullable=false,length=24) private String status="PENDING_REVIEW";
 @Column(name="rejection_reason",length=500) private String rejectionReason;
 @Column(name="submitted_at",nullable=false,updatable=false) private Instant submittedAt;
 @Column(name="reviewed_at") private Instant reviewedAt;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="reviewed_by") private UserAccount reviewedBy;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected Seller(){}
 public Seller(UserAccount user,String legalName,String storeName,String businessEmail,String phone,String businessAddress,String gstin){this.user=user;this.legalName=legalName;this.storeName=storeName;this.businessEmail=businessEmail;this.phone=phone;this.businessAddress=businessAddress;this.gstin=gstin;this.submittedAt=Instant.now();}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;if(submittedAt==null)submittedAt=createdAt;}
 @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public UserAccount getUser(){return user;} public String getLegalName(){return legalName;} public String getStoreName(){return storeName;} public String getBusinessEmail(){return businessEmail;} public String getPhone(){return phone;} public String getBusinessAddress(){return businessAddress;} public String getGstin(){return gstin;} public String getStatus(){return status;} public String getRejectionReason(){return rejectionReason;} public Instant getSubmittedAt(){return submittedAt;} public Instant getReviewedAt(){return reviewedAt;}
 public void approve(UserAccount admin){this.status="APPROVED";this.reviewedBy=admin;this.reviewedAt=Instant.now();this.rejectionReason=null;}
 public void reject(UserAccount admin,String reason){this.status="REJECTED";this.reviewedBy=admin;this.reviewedAt=Instant.now();this.rejectionReason=reason;}
}
