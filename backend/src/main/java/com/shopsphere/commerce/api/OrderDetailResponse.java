package com.shopsphere.commerce.api;
import java.time.Instant;
import java.util.List;
public record OrderDetailResponse(String orderNumber,String status,long subtotalMinor,long shippingMinor,long totalMinor,String currency,String recipientName,String phone,String addressLine1,String addressLine2,String city,String state,String postalCode,String country,Instant createdAt,List<SellerOrderResponse> sellerOrders){}
