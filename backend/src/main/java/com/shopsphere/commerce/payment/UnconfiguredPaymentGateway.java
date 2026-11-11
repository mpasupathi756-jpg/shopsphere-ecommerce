package com.shopsphere.commerce.payment;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
@Component @Profile("!local")
public class UnconfiguredPaymentGateway implements PaymentGateway {
 @Override public String providerName(){return "UNCONFIGURED";}
 @Override public PaymentIntent createIntent(String orderNumber,long amountMinor,String currency,String idempotencyKey){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"A marketplace payment provider has not been configured. No order was placed and no stock was reserved.");}
}
