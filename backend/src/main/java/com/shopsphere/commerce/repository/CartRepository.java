package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.Cart;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CartRepository extends JpaRepository<Cart,UUID>{Optional<Cart> findByUser_Id(UUID userId);}
