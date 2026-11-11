package com.shopsphere.commerce.api;
import com.shopsphere.commerce.service.WishlistService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/wishlist")
public class WishlistController {
 private final WishlistService service;
 public WishlistController(WishlistService service){this.service=service;}
 @GetMapping public List<WishlistItemResponse> get(@AuthenticationPrincipal Jwt jwt){return service.get(UUID.fromString(jwt.getSubject()));}
 @PostMapping("/products/{productId}") public List<WishlistItemResponse> add(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID productId){return service.add(UUID.fromString(jwt.getSubject()),productId);}
 @DeleteMapping("/products/{productId}") public List<WishlistItemResponse> remove(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID productId){return service.remove(UUID.fromString(jwt.getSubject()),productId);}
}
