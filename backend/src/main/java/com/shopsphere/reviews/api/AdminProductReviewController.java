package com.shopsphere.reviews.api;
import com.shopsphere.reviews.service.ProductReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/admin/reviews")
public class AdminProductReviewController {
 private final ProductReviewService service;
 public AdminProductReviewController(ProductReviewService service){this.service=service;}
 @GetMapping("/reports") public Page<ReviewReportResponse> reports(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.reportQueue(page,size);}
 @PatchMapping("/{reviewId}/moderation") public void moderate(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID reviewId,@Valid @RequestBody ModerationRequest request){service.moderate(UUID.fromString(jwt.getSubject()),reviewId,request);}
 @PatchMapping("/reports/{reportId}") public void resolve(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID reportId,@Valid @RequestBody ReportResolutionRequest request){service.resolveReport(UUID.fromString(jwt.getSubject()),reportId,request);}
}