package com.shopsphere.marketplace.api;
import java.util.UUID;
public record AdminListingResponse(UUID id,UUID productId,String productTitle,UUID sellerId,String storeName,String sellerSku,long priceMinor,String currency,String status,int availableQuantity,String rejectionReason){}
