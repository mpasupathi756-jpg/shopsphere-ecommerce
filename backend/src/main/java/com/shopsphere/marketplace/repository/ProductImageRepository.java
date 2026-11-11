package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.ProductImage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductImageRepository extends JpaRepository<ProductImage,UUID>{List<ProductImage> findAllByProduct_IdOrderBySortOrderAsc(UUID productId);}
