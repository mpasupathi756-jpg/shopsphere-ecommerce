package com.shopsphere.commerce.api;
import java.util.List;
import java.util.UUID;
public record SellerCheckoutGroup(UUID sellerId,String storeName,List<CheckoutPreviewLine> items,long subtotalMinor,long shippingMinor,long totalMinor){}
