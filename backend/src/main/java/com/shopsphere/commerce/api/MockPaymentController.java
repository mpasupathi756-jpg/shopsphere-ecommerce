package com.shopsphere.commerce.api;
import com.shopsphere.commerce.service.PaymentService;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @Profile("local") @RequestMapping("/api/v1/payments/mock")
public class MockPaymentController {
 private final PaymentService service;
 public MockPaymentController(PaymentService service){this.service=service;}
 @PostMapping("/{orderNumber}/success") public OrderDetailResponse success(@AuthenticationPrincipal Jwt jwt,@PathVariable String orderNumber){return service.confirmMockSuccess(UUID.fromString(jwt.getSubject()),orderNumber);}
}
