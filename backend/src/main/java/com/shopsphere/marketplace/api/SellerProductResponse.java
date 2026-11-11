package com.shopsphere.marketplace.api;
import java.util.UUID;
public record SellerProductResponse(UUID id,String title,String slug,String categoryName,String status,String rejectionReason){}
