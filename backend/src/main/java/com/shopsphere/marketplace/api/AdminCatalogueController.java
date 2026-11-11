package com.shopsphere.marketplace.api;
import com.shopsphere.marketplace.service.SellerCatalogueService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/admin/catalog")
public class AdminCatalogueController {
 private final SellerCatalogueService service;
 public AdminCatalogueController(SellerCatalogueService service){this.service=service;}
 @GetMapping("/products/pending") public List<SellerProductResponse> pendingProducts(){return service.pendingProducts();}
 @PatchMapping("/products/{productId}/approve") public AdminProductResponse approveProduct(@PathVariable UUID productId){return service.approveProduct(productId);}
 @PatchMapping("/products/{productId}/reject") public AdminProductResponse rejectProduct(@PathVariable UUID productId,@Valid @RequestBody ProductReviewRequest request){return service.rejectProduct(productId,request.reason());}
 @GetMapping("/listings/pending") public List<AdminListingResponse> pendingListings(){return service.pendingListings();}
 @PatchMapping("/listings/{listingId}/approve") public AdminListingResponse approveListing(@PathVariable UUID listingId){return service.approveListing(listingId);}
 @PatchMapping("/listings/{listingId}/reject") public AdminListingResponse rejectListing(@PathVariable UUID listingId,@Valid @RequestBody ProductReviewRequest request){return service.rejectListing(listingId,request.reason());}
}
