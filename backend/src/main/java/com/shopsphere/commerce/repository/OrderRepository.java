package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.CustomerOrder;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface OrderRepository extends JpaRepository<CustomerOrder,UUID>{
 List<CustomerOrder> findAllByCustomer_IdOrderByCreatedAtDesc(UUID customerId);
 Optional<CustomerOrder> findByOrderNumberAndCustomer_Id(String orderNumber,UUID customerId);
 Optional<CustomerOrder> findByCustomer_IdAndIdempotencyKey(UUID customerId,String idempotencyKey);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select o from CustomerOrder o where o.status='PAYMENT_PENDING' and o.expiresAt < :now")
 List<CustomerOrder> findDueOrdersForUpdate(@Param("now") Instant now);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select o from CustomerOrder o where o.orderNumber=:orderNumber and o.customer.id=:customerId")
 Optional<CustomerOrder> findByOrderNumberForUpdate(@Param("orderNumber") String orderNumber,@Param("customerId") UUID customerId);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select o from CustomerOrder o where o.id=:id")
 Optional<CustomerOrder> findByIdForUpdate(@Param("id") UUID id);
}
