package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.ProductAttribute;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute,UUID>{List<ProductAttribute> findAllByProduct_IdOrderBySortOrderAsc(UUID productId);}
