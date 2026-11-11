package com.shopsphere.commerce.api;
import java.time.Instant;
public record OrderSummaryResponse(String orderNumber,String status,long totalMinor,String currency,Instant createdAt,int sellerOrderCount){}
