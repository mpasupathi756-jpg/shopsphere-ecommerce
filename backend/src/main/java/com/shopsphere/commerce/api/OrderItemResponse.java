package com.shopsphere.commerce.api;
public record OrderItemResponse(String productTitle,String sellerSku,int quantity,long unitPriceMinor,long lineTotalMinor,String currency){}
