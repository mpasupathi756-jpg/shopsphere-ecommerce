package com.shopsphere.marketplace.api;
import java.time.Instant;
import java.util.UUID;
public record SellerApplicationResponse(UUID id,UUID userId,String storeName,String legalName,String businessEmail,String phone,String status,String rejectionReason,Instant submittedAt,Instant reviewedAt){}
