package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.OrderItem;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface OrderItemRepository extends JpaRepository<OrderItem,UUID>{
 List<OrderItem> findAllBySellerOrder_Id(UUID sellerOrderId);
 @Query("select i from OrderItem i join i.sellerOrder s join s.order o where o.customer.id=:customerId and i.product.slug=:slug and s.status='DELIVERED' order by s.updatedAt desc")
 List<OrderItem> findDeliveredPurchaseForReview(@Param("customerId") UUID customerId,@Param("slug") String productSlug);
}