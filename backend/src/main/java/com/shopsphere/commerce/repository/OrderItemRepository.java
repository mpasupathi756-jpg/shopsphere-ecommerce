package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderItemRepository extends JpaRepository<OrderItem,UUID>{List<OrderItem> findAllBySellerOrder_Id(UUID sellerOrderId);}
