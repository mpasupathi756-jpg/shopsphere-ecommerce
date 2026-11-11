package com.shopsphere.commerce.api;
import java.util.List;
public record CheckoutPreviewResponse(AddressResponse shippingAddress,List<SellerCheckoutGroup> sellerOrders,long subtotalMinor,long shippingMinor,long discountMinor,long totalMinor,String currency,boolean paymentEnabled,String paymentNotice){}
