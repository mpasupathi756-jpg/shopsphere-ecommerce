package com.shopsphere.marketplace.api;
import java.util.UUID;
public record AdminProductResponse(UUID id,String title,String slug,String categoryName,String brandName,String status,String rejectionReason){}
