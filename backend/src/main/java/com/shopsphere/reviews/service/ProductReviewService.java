package com.shopsphere.reviews.service;

import com.shopsphere.commerce.domain.OrderItem;
import com.shopsphere.commerce.repository.OrderItemRepository;
import com.shopsphere.identity.domain.UserAccount;
import com.shopsphere.identity.repository.UserAccountRepository;
import com.shopsphere.marketplace.domain.Product;
import com.shopsphere.marketplace.repository.ProductRepository;
import com.shopsphere.reviews.api.*;
import com.shopsphere.reviews.domain.*;
import com.shopsphere.reviews.repository.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductReviewService {
 private final ProductReviewRepository reviews;
 private final ProductReviewReportRepository reports;
 private final ProductRepository products;
 private final UserAccountRepository users;
 private final OrderItemRepository orderItems;
 public ProductReviewService(ProductReviewRepository reviews,ProductReviewReportRepository reports,ProductRepository products,UserAccountRepository users,OrderItemRepository orderItems){this.reviews=reviews;this.reports=reports;this.products=products;this.users=users;this.orderItems=orderItems;}

 @Transactional(readOnly=true)
 public ReviewEligibilityResponse eligibility(UUID customerId,String slug){
  activeUser(customerId); Product product=activeProduct(slug);
  boolean already=reviews.existsByCustomer_IdAndProduct_Id(customerId,product.getId());
  boolean delivered=!orderItems.findDeliveredPurchaseForReview(customerId,slug).isEmpty();
  return new ReviewEligibilityResponse(delivered&&!already,already,already?\"You have already reviewed this product\":delivered?\"You can review this delivered purchase\":\"A delivered purchase is required before reviewing\");
 }
 @Transactional
 public ReviewResponse create(UUID customerId,String slug,ReviewRequest request){
  UserAccount customer=activeUser(customerId);
  Product product=activeProduct(slug);
  if(reviews.existsByCustomer_IdAndProduct_Id(customerId,product.getId()))throw new ResponseStatusException(HttpStatus.CONFLICT,"You have already reviewed this product");
  OrderItem purchase=orderItems.findDeliveredPurchaseForReview(customerId,slug).stream().findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN,"A delivered purchase of this product is required before reviewing"));
  return response(reviews.save(new ProductReview(product,customer,purchase,request.rating(),clean(request.title()),clean(request.body()))));
 }

 @Transactional(readOnly=true)
 public Page<ReviewResponse> publicReviews(String slug,int page,int size){
  bounds(page,size); activeProduct(slug);
  return reviews.findAllByProduct_SlugAndStatus(slug,"PUBLISHED",PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"))).map(this::response);
 }
 @Transactional(readOnly=true)
 public Page<ReviewResponse> myReviews(UUID customerId,int page,int size){
  bounds(page,size); activeUser(customerId);
  return reviews.findAllByCustomer_IdOrderByCreatedAtDesc(customerId,PageRequest.of(page,size)).map(this::response);
 }
 @Transactional
 public ReviewResponse edit(UUID customerId,UUID reviewId,ReviewRequest request){
  activeUser(customerId);
  ProductReview review=reviews.findByIdAndCustomer_Id(reviewId,customerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Review not found"));
  try{review.edit(request.rating(),clean(request.title()),clean(request.body()));}catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}
  return response(review);
 }
 @Transactional
 public void delete(UUID customerId,UUID reviewId){
  activeUser(customerId);
  ProductReview review=reviews.findByIdAndCustomer_Id(reviewId,customerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Review not found"));
  review.moderate("REMOVED","Removed by review author",users.getReferenceById(customerId));
 }
 @Transactional
 public void report(UUID reporterId,UUID reviewId,ReviewReportRequest request){
  UserAccount reporter=activeUser(reporterId);
  ProductReview review=reviews.findById(reviewId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Review not found"));
  if(review.getCustomer().getId().equals(reporterId))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You cannot report your own review");
  if(!"PUBLISHED".equals(review.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Only published reviews can be reported");
  if(reports.existsByReview_IdAndReporter_Id(reviewId,reporterId))throw new ResponseStatusException(HttpStatus.CONFLICT,"You have already reported this review");
  reports.save(new ProductReviewReport(review,reporter,request.reasonCode(),blankToNull(request.details())));
 }
 @Transactional(readOnly=true)
 public Page<ReviewReportResponse> reportQueue(int page,int size){
  bounds(page,size);
  return reports.findAllByStatusOrderByCreatedAtAsc("OPEN",PageRequest.of(page,size)).map(this::reportResponse);
 }
 @Transactional
 public void moderate(UUID adminId,UUID reviewId,ModerationRequest request){
  UserAccount admin=activeUser(adminId);
  ProductReview review=reviews.findById(reviewId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Review not found"));
  review.moderate(request.action(),clean(request.reason()),admin);
 }
 @Transactional
 public void resolveReport(UUID adminId,UUID reportId,ReportResolutionRequest request){
  UserAccount admin=activeUser(adminId);
  ProductReviewReport report=reports.findById(reportId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Report not found"));
  if(!"OPEN".equals(report.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Report has already been resolved");
  report.resolve(request.action(),blankToNull(request.note()),admin);
 }
 @Transactional(readOnly=true)
 public ReviewAggregateResponse aggregate(UUID productId){return new ReviewAggregateResponse(reviews.averagePublished(productId),reviews.countPublished(productId));}
 private UserAccount activeUser(UUID id){UserAccount user=users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found"));if(!user.isActive())throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account is not active");return user;}
 private Product activeProduct(String slug){return products.findBySlugAndProductStatus(slug,"ACTIVE").orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));}
 private ReviewResponse response(ProductReview r){return new ReviewResponse(r.getId(),r.getProduct().getSlug(),r.getCustomer().getDisplayName(),r.getRating(),r.getTitle(),r.getBody(),true,r.getCreatedAt());}
 private ReviewReportResponse reportResponse(ProductReviewReport r){return new ReviewReportResponse(r.getId(),r.getReview().getId(),r.getReview().getProduct().getTitle(),r.getReview().getTitle(),r.getReporter().getDisplayName(),r.getReasonCode(),r.getDetails(),r.getStatus(),r.getCreatedAt());}
 private void bounds(int page,int size){if(page<0||size<1||size>50)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page must be non-negative and page size must be between 1 and 50");}
 private String clean(String value){return value.trim();}
 private String blankToNull(String value){return value==null||value.isBlank()?null:value.trim();}
}