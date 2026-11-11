package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.InventoryMovement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement,UUID>{}
