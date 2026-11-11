package com.shopsphere.commerce.api;
import java.util.List;
import java.util.UUID;
public record SellerOrderResponse(UUID sellerOrderId,String orderNumber,String storeName,String status,long subtotalMinor,long totalMinor,String carrier,String trackingNumber,List<OrderItemResponse> items){}
