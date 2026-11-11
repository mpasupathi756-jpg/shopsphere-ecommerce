package com.shopsphere.commerce.api;
import java.time.Instant;
public record CheckoutOrderResponse(String orderNumber,String status,long subtotalMinor,long shippingMinor,long totalMinor,String paymentProvider,String paymentStatus,String checkoutUrl,Instant expiresAt){}
