package com.shopsphere.marketplace.api;
import com.shopsphere.marketplace.service.SellerCatalogueService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/seller")
public class SellerCatalogueController {
 private final SellerCatalogueService service;
 public SellerCatalogueController(SellerCatalogueService service){this.service=service;}
 @PostMapping("/products") public SellerProductResponse submitProduct(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody ProductSubmissionRequest request){return service.submitProduct(UUID.fromString(jwt.getSubject()),request);}
 @GetMapping("/products") public List<SellerProductResponse> myProducts(@AuthenticationPrincipal Jwt jwt){return service.myProducts(UUID.fromString(jwt.getSubject()));}
 @PostMapping("/listings") public ListingResponse createListing(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody CreateListingRequest request){return service.createListing(UUID.fromString(jwt.getSubject()),request);}
 @GetMapping("/listings") public List<ListingResponse> myListings(@AuthenticationPrincipal Jwt jwt){return service.myListings(UUID.fromString(jwt.getSubject()));}
 @PatchMapping("/listings/{listingId}/pause") public ListingResponse pause(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID listingId){return service.pauseListing(UUID.fromString(jwt.getSubject()),listingId);}
}
