package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.Wishlist;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WishlistRepository extends JpaRepository<Wishlist,UUID>{Optional<Wishlist> findByUser_Id(UUID userId);}
