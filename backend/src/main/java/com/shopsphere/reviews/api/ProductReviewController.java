package com.shopsphere.reviews.api;
import com.shopsphere.reviews.service.ProductReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@RestController @RequestMapping("/api/v1")
public class ProductReviewController {
 private final ProductReviewService service;
 public ProductReviewController(ProductReviewService service){this.service=service;}
 @GetMapping("/products/{slug}/reviews") public Page<ReviewResponse> list(@PathVariable String slug,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){return service.publicReviews(slug,page,size);}
 @GetMapping("/products/{slug}/reviews/eligibility") public ReviewEligibilityResponse eligibility(@AuthenticationPrincipal Jwt jwt,@PathVariable String slug){return service.eligibility(UUID.fromString(jwt.getSubject()),slug);}
 @PostMapping("/products/{slug}/reviews") public ReviewResponse create(@AuthenticationPrincipal Jwt jwt,@PathVariable String slug,@Valid @RequestBody ReviewRequest request){return service.create(UUID.fromString(jwt.getSubject()),slug,request);}
 @GetMapping("/me/reviews") public Page<ReviewResponse> mine(@AuthenticationPrincipal Jwt jwt,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.myReviews(UUID.fromString(jwt.getSubject()),page,size);}
 @PatchMapping("/me/reviews/{reviewId}") public ReviewResponse edit(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID reviewId,@Valid @RequestBody ReviewRequest request){return service.edit(UUID.fromString(jwt.getSubject()),reviewId,request);}
 @ResponseStatus(HttpStatus.NO_CONTENT) @DeleteMapping("/me/reviews/{reviewId}") public void delete(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID reviewId){service.delete(UUID.fromString(jwt.getSubject()),reviewId);}
 @ResponseStatus(HttpStatus.NO_CONTENT) @PostMapping("/reviews/{reviewId}/reports") public void report(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID reviewId,@Valid @RequestBody ReviewReportRequest request){service.report(UUID.fromString(jwt.getSubject()),reviewId,request);}
}