package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.SellerOrder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SellerOrderRepository extends JpaRepository<SellerOrder,UUID>{List<SellerOrder> findAllByOrder_Id(UUID orderId);}
