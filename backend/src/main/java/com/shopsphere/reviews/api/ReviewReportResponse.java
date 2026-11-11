package com.shopsphere.reviews.api;
import java.time.Instant;
import java.util.UUID;
public record ReviewReportResponse(UUID id,UUID reviewId,String productTitle,String reviewTitle,String reporterName,String reasonCode,String details,String status,Instant createdAt) {}