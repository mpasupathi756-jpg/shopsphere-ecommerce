package com.shopsphere.marketplace.api;
import com.shopsphere.marketplace.service.SellerService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/admin/sellers")
public class AdminSellerController {
 private final SellerService service;
 public AdminSellerController(SellerService service){this.service=service;}
 @GetMapping public List<SellerApplicationResponse> list(@RequestParam(required=false) String status){return service.list(status);}
 @PatchMapping("/{sellerId}/approve") public SellerApplicationResponse approve(@PathVariable UUID sellerId,@AuthenticationPrincipal Jwt jwt){return service.approve(sellerId,UUID.fromString(jwt.getSubject()));}
 @PatchMapping("/{sellerId}/reject") public SellerApplicationResponse reject(@PathVariable UUID sellerId,@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody RejectSellerRequest request){return service.reject(sellerId,UUID.fromString(jwt.getSubject()),request.reason());}
}
