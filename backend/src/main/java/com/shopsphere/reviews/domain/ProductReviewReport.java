package com.shopsphere.reviews.domain;

import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name="product_review_reports",uniqueConstraints=@UniqueConstraint(name="ux_review_reports_review_reporter",columnNames={"review_id","reporter_id"}))
public class ProductReviewReport {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="review_id",nullable=false) private ProductReview review;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="reporter_id",nullable=false) private UserAccount reporter;
 @Column(name="reason_code",nullable=false,length=24) private String reasonCode;
 @Column(length=500) private String details;
 @Column(nullable=false,length=16) private String status="OPEN";
 @Column(name="resolution_note",length=500) private String resolutionNote;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="resolved_by") private UserAccount resolvedBy;
 @Column(name="resolved_at") private Instant resolvedAt;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected ProductReviewReport(){}
 public ProductReviewReport(ProductReview review,UserAccount reporter,String reasonCode,String details){this.review=review;this.reporter=reporter;this.reasonCode=reasonCode;this.details=details;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;}
 @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public void resolve(String status,String note,UserAccount admin){if(!java.util.Set.of("DISMISSED","ACTIONED").contains(status))throw new IllegalArgumentException("Invalid report resolution");this.status=status;this.resolutionNote=note;this.resolvedBy=admin;this.resolvedAt=Instant.now();}
 public UUID getId(){return id;} public ProductReview getReview(){return review;} public UserAccount getReporter(){return reporter;} public String getReasonCode(){return reasonCode;} public String getDetails(){return details;} public String getStatus(){return status;} public String getResolutionNote(){return resolutionNote;} public Instant getCreatedAt(){return createdAt;}
}