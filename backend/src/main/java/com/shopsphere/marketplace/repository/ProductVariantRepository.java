package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.ProductVariant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductVariantRepository extends JpaRepository<ProductVariant,UUID>{Optional<ProductVariant> findByIdAndProduct_Id(UUID id,UUID productId);}
