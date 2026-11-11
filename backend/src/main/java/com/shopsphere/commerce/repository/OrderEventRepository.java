package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.OrderEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderEventRepository extends JpaRepository<OrderEvent,UUID>{List<OrderEvent> findAllByOrder_IdOrderByCreatedAtAsc(UUID orderId);}
