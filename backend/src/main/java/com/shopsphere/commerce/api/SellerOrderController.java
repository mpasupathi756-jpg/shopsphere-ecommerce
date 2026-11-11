package com.shopsphere.commerce.api;
import com.shopsphere.commerce.service.SellerOrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/seller/orders")
public class SellerOrderController {
 private final SellerOrderService service;
 public SellerOrderController(SellerOrderService service){this.service=service;}
 @GetMapping public List<SellerOrderResponse> list(@AuthenticationPrincipal Jwt jwt){return service.list(UUID.fromString(jwt.getSubject()));}
 @PatchMapping("/{sellerOrderId}/processing") public SellerOrderResponse processing(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID sellerOrderId){return service.processing(UUID.fromString(jwt.getSubject()),sellerOrderId);}
 @PatchMapping("/{sellerOrderId}/ship") public SellerOrderResponse ship(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID sellerOrderId,@Valid @RequestBody ShipOrderRequest request){return service.ship(UUID.fromString(jwt.getSubject()),sellerOrderId,request.carrier(),request.trackingNumber());}
 @PatchMapping("/{sellerOrderId}/delivered") public SellerOrderResponse delivered(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID sellerOrderId){return service.delivered(UUID.fromString(jwt.getSubject()),sellerOrderId);}
}
