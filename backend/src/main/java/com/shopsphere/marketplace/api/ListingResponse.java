package com.shopsphere.marketplace.api;
import java.util.UUID;
public record ListingResponse(UUID id,UUID productId,String productTitle,String sellerSku,long priceMinor,String currency,String status,int availableQuantity){}
