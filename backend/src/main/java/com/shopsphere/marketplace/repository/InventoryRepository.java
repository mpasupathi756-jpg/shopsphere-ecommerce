package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.Inventory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InventoryRepository extends JpaRepository<Inventory,UUID>{Optional<Inventory> findByListing_Id(UUID listingId);
 @jakarta.persistence.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @org.springframework.data.jpa.repository.Query("select stock from Inventory stock where stock.listing.id=:listingId")
 Optional<Inventory> findByListingIdForUpdate(@org.springframework.data.repository.query.Param("listingId") UUID listingId);}
