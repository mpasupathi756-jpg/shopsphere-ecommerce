package com.shopsphere.commerce.payment;
public record PaymentIntent(String provider,String providerReference,String checkoutUrl){}
