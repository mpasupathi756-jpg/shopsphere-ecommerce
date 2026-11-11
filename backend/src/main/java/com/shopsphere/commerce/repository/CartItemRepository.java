package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.CartItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface CartItemRepository extends JpaRepository<CartItem,UUID>{List<CartItem> findAllByCart_IdOrderByCreatedAtAsc(UUID cartId);Optional<CartItem> findByIdAndCart_User_Id(UUID id,UUID userId);void deleteAllByCart_Id(UUID cartId);@Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select item from CartItem item join fetch item.listing listing where item.cart.id=:cartId") List<CartItem> findAllForUpdate(@Param("cartId") UUID cartId);}
