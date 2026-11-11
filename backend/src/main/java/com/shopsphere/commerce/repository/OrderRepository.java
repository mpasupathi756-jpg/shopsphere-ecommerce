package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.CustomerOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<CustomerOrder,UUID>{List<CustomerOrder> findAllByCustomer_IdOrderByCreatedAtDesc(UUID customerId);Optional<CustomerOrder> findByOrderNumberAndCustomer_Id(String orderNumber,UUID customerId);Optional<CustomerOrder> findByCustomer_IdAndIdempotencyKey(UUID customerId,String idempotencyKey);
 java.util.List<CustomerOrder> findAllByStatusAndExpiresAtBefore(String status,java.time.Instant expiresAt);
 @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @org.springframework.data.jpa.repository.Query("select o from CustomerOrder o where o.orderNumber=:orderNumber and o.customer.id=:customerId")
 Optional<CustomerOrder> findByOrderNumberForUpdate(@org.springframework.data.repository.query.Param("orderNumber") String orderNumber,@org.springframework.data.repository.query.Param("customerId") UUID customerId);}
