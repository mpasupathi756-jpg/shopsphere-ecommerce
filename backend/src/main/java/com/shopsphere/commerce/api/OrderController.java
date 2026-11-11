package com.shopsphere.commerce.api;
import com.shopsphere.commerce.service.OrderQueryService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/orders")
public class OrderController {
 private final OrderQueryService service;
 public OrderController(OrderQueryService service){this.service=service;}
 @GetMapping public List<OrderSummaryResponse> list(@AuthenticationPrincipal Jwt jwt){return service.customerOrders(UUID.fromString(jwt.getSubject()));}
 @GetMapping("/{orderNumber}") public OrderDetailResponse detail(@AuthenticationPrincipal Jwt jwt,@PathVariable String orderNumber){return service.customerOrder(UUID.fromString(jwt.getSubject()),orderNumber);}
}
