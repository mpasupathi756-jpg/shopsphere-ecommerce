package com.shopsphere.marketplace.api;
import com.shopsphere.marketplace.service.SellerService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/v1/seller-applications")
public class SellerApplicationController {
 private final SellerService service;
 public SellerApplicationController(SellerService service){this.service=service;}
 @PostMapping public org.springframework.http.ResponseEntity<SellerApplicationResponse> submit(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody SellerApplicationRequest request){return org.springframework.http.ResponseEntity.status(HttpStatus.CREATED).body(service.submit(userId(jwt),request));}
 @GetMapping("/me") public SellerApplicationResponse mine(@AuthenticationPrincipal Jwt jwt){return service.mine(userId(jwt));}
 private UUID userId(Jwt jwt){try{return UUID.fromString(jwt.getSubject());}catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid account token");}}
}
