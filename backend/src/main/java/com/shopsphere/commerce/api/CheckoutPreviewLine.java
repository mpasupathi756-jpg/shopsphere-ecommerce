package com.shopsphere.commerce.api;
import java.util.UUID;
public record CheckoutPreviewLine(UUID listingId,String productTitle,String sellerSku,int quantity,long unitPriceMinor,long lineTotalMinor,String currency){}
