package com.shopsphere.reviews.repository;

import com.shopsphere.reviews.domain.ProductReviewReport;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewReportRepository extends JpaRepository<ProductReviewReport,UUID> {
 boolean existsByReview_IdAndReporter_Id(UUID reviewId,UUID reporterId);
 Page<ProductReviewReport> findAllByStatusOrderByCreatedAtAsc(String status,Pageable pageable);
}