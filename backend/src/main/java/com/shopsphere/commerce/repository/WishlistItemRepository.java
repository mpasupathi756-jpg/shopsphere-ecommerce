package com.shopsphere.commerce.repository;
import com.shopsphere.commerce.domain.WishlistItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WishlistItemRepository extends JpaRepository<WishlistItem,UUID>{List<WishlistItem> findAllByWishlist_IdOrderByCreatedAtDesc(UUID wishlistId);Optional<WishlistItem> findByWishlist_IdAndProduct_Id(UUID wishlistId,UUID productId);void deleteByWishlist_IdAndProduct_Id(UUID wishlistId,UUID productId);}
