package com.shopsphere.commerce.payment;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Component @Profile("local")
public class MockPaymentGateway implements PaymentGateway {
 @Override public String providerName(){return "MOCK";}
 @Override public PaymentIntent createIntent(String orderNumber,long amountMinor,String currency,String idempotencyKey){return new PaymentIntent("MOCK","MOCK-"+orderNumber,"http://localhost:5173/mock-payment/"+orderNumber);}
}
