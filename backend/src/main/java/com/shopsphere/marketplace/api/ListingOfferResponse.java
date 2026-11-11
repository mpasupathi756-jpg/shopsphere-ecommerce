package com.shopsphere.marketplace.api;
import java.util.UUID;
public record ListingOfferResponse(UUID listingId,UUID sellerId,String storeName,String sellerSku,long priceMinor,String currency,String condition,int availableQuantity,int shippingDaysMin,int shippingDaysMax,int returnWindowDays,String warrantyText){}
