package com.shopsphere.marketplace.repository;
import com.shopsphere.marketplace.domain.Brand;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BrandRepository extends JpaRepository<Brand,UUID>{Optional<Brand> findBySlug(String slug);}
