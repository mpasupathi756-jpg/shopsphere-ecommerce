package com.shopsphere.commerce.payment;
public interface PaymentGateway {
 String providerName();
 PaymentIntent createIntent(String orderNumber,long amountMinor,String currency,String idempotencyKey);
}
