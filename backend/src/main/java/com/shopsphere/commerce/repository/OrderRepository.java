package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.CustomerOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<CustomerOrder,UUID>{List<CustomerOrder> findAllByCustomer_IdOrderByCreatedAtDesc(UUID customerId);Optional<CustomerOrder> findByOrderNumberAndCustomer_Id(String orderNumber,UUID customerId);Optional<CustomerOrder> findByCustomer_IdAndIdempotencyKey(UUID customerId,String idempotencyKey);}
