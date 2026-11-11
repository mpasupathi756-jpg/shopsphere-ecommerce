package com.shopsphere.commerce.api;
import java.util.UUID;
public record CartItemResponse(UUID id,UUID listingId,UUID productId,String productTitle,String productSlug,String storeName,long unitPriceMinor,String currency,int quantity,int availableQuantity,long lineTotalMinor){}
