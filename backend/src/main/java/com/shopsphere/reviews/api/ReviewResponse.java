package com.shopsphere.reviews.api;
import java.time.Instant;
import java.util.UUID;
public record ReviewResponse(UUID id,String productSlug,String authorName,int rating,String title,String body,boolean verifiedPurchase,Instant createdAt) {}