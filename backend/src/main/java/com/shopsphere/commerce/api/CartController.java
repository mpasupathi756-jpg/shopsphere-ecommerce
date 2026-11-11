package com.shopsphere.commerce.api;
import com.shopsphere.commerce.service.CartService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/cart")
public class CartController {
 private final CartService service;
 public CartController(CartService service){this.service=service;}
 @GetMapping public CartResponse get(@AuthenticationPrincipal Jwt jwt){return service.get(UUID.fromString(jwt.getSubject()));}
 @PostMapping("/items") public CartResponse add(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody CartItemRequest request){return service.add(UUID.fromString(jwt.getSubject()),request);}
 @PatchMapping("/items/{itemId}") public CartResponse update(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID itemId,@Valid @RequestBody UpdateCartItemRequest request){return service.update(UUID.fromString(jwt.getSubject()),itemId,request.quantity());}
 @DeleteMapping("/items/{itemId}") public CartResponse remove(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID itemId){return service.remove(UUID.fromString(jwt.getSubject()),itemId);}
}
