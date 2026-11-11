package com.shopsphere.reviews.repository;

import com.shopsphere.reviews.domain.ProductReview;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductReviewRepository extends JpaRepository<ProductReview,UUID> {
 boolean existsByCustomer_IdAndProduct_Id(UUID customerId,UUID productId);
 Optional<ProductReview> findByIdAndCustomer_Id(UUID id,UUID customerId);
 Page<ProductReview> findAllByProduct_SlugAndStatus(String slug,String status,Pageable pageable);
 Page<ProductReview> findAllByCustomer_IdOrderByCreatedAtDesc(UUID customerId,Pageable pageable);
 @Query("select count(r) from ProductReview r where r.product.id=:productId and r.status='PUBLISHED'")
 long countPublished(@Param("productId") UUID productId);
 @Query("select avg(r.rating) from ProductReview r where r.product.id=:productId and r.status='PUBLISHED'")
 Double averagePublished(@Param("productId") UUID productId);
 @Query("select r.product.id, avg(r.rating), count(r) from ProductReview r where r.product.id in :productIds and r.status='PUBLISHED' group by r.product.id")
 List<Object[]> aggregateForProducts(@Param("productIds") Collection<UUID> productIds);
 Page<ProductReview> findAllByStatusOrderByUpdatedAtDesc(String status,Pageable pageable);
}