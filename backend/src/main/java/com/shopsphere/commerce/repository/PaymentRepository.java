package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentRepository extends JpaRepository<Payment,UUID>{Optional<Payment> findByOrder_Id(UUID orderId);}
