package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.Inventory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InventoryRepository extends JpaRepository<Inventory,UUID>{Optional<Inventory> findByListing_Id(UUID listingId);}
