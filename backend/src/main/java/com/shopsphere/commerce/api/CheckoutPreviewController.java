package com.shopsphere.commerce.api;
import com.shopsphere.commerce.service.CheckoutPreviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/checkout")
public class CheckoutPreviewController {
 private final CheckoutPreviewService service;
 public CheckoutPreviewController(CheckoutPreviewService service){this.service=service;}
 @PostMapping("/preview") public CheckoutPreviewResponse preview(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody CheckoutPreviewRequest request){return service.preview(UUID.fromString(jwt.getSubject()),request.addressId());}
}
